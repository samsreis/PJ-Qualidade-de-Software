package br.senac.organizacao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import br.senac.organizacao.domain.Material;
import br.senac.organizacao.domain.Reserva;
import br.senac.organizacao.repository.MaterialRepository;
import br.senac.organizacao.repository.ReservaRepository;
import br.senac.organizacao.repository.SalaRepository;
import br.senac.organizacao.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ReservaApiTest {
	@Autowired
	MockMvc mockMvc;
	@Autowired
	ReservaRepository reservaRepository;
	@Autowired
	SalaRepository salaRepository;
	@Autowired
	ProfessorRepository professorRepository;
	@Autowired
	MaterialRepository materialRepository;

	@BeforeEach
	void limparReservas() {
		reservaRepository.deleteAll();
	}

	@Test
	void postSemAuthDeveRetornar401() throws Exception {
		mockMvc.perform(post("/reservas").contentType(MediaType.APPLICATION_JSON).content(reservaJson(null)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void postComSolicitanteEMaterialRestritoDeveRetornar403() throws Exception {
		Material material = materialRepository.findAll().stream().filter(Material::isRestrito).findFirst().orElseThrow();
		mockMvc.perform(post("/reservas").header("Authorization", basic("solicitante@teste.com"))
			.contentType(MediaType.APPLICATION_JSON).content(reservaJson(material.getId())))
			.andExpect(status().isForbidden());
	}

	@Test
	void postValidoDeveRetornar201() throws Exception {
		Material material = materialRepository.findAll().stream().filter(item -> !item.isRestrito()).findFirst().orElseThrow();
		mockMvc.perform(post("/reservas").header("Authorization", basic("solicitante@teste.com"))
			.contentType(MediaType.APPLICATION_JSON).content(reservaJson(material.getId())))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.solicitante.senha").doesNotExist());
	}

	@Test
	void postComIntervaloInvalidoDeveRetornar400() throws Exception {
		LocalDateTime inicio = LocalDateTime.now().plusDays(30).withSecond(0).withNano(0);
		String json = reservaJson(null, inicio, inicio.minusHours(1));
		mockMvc.perform(post("/reservas").header("Authorization", basic("solicitante@teste.com"))
			.contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isBadRequest());
	}

	@Test
	void postSemInicioDeveRetornar400() throws Exception {
		mockMvc.perform(post("/reservas").header("Authorization", basic("solicitante@teste.com"))
			.contentType(MediaType.APPLICATION_JSON).content("{\"fim\":\"2026-12-01T11:00:00\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void postComReservaSobrepostaDeveRetornar409() throws Exception {
		String json = reservaJson(null);
		mockMvc.perform(post("/reservas").header("Authorization", basic("solicitante@teste.com"))
			.contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isCreated());
		mockMvc.perform(post("/reservas").header("Authorization", basic("solicitante@teste.com"))
			.contentType(MediaType.APPLICATION_JSON).content(json))
			.andExpect(status().isConflict());
	}

	@Test
	void putComReservaSobrepostaDeveRetornar409() throws Exception {
		LocalDateTime inicio = LocalDateTime.now().plusDays(30).withSecond(0).withNano(0);
		String primeira = reservaJson(null, inicio, inicio.plusHours(1));
		String segunda = reservaJson(null, inicio.plusHours(2), inicio.plusHours(3));
		String autorizacao = basic("solicitante@teste.com");
		mockMvc.perform(post("/reservas").header("Authorization", autorizacao)
			.contentType(MediaType.APPLICATION_JSON).content(primeira))
			.andExpect(status().isCreated());
		mockMvc.perform(post("/reservas").header("Authorization", autorizacao)
			.contentType(MediaType.APPLICATION_JSON).content(segunda))
			.andExpect(status().isCreated());
		Reserva reservaAtualizada = reservaRepository.findAll().stream()
			.filter(reserva -> reserva.getInicio().equals(inicio.plusHours(2))).findFirst().orElseThrow();

		mockMvc.perform(put("/reservas/" + reservaAtualizada.getId()).header("Authorization", autorizacao)
			.contentType(MediaType.APPLICATION_JSON).content(primeira))
			.andExpect(status().isConflict());
	}

	@Test
	void putValidoPreservaEstadoEPermiteManterMesmoHorario() throws Exception {
		LocalDateTime inicio = LocalDateTime.now().plusDays(30).withSecond(0).withNano(0);
		String autorizacao = basic("solicitante@teste.com");
		mockMvc.perform(post("/reservas").header("Authorization", autorizacao)
			.contentType(MediaType.APPLICATION_JSON).content(reservaJson(null, inicio, inicio.plusHours(1))))
			.andExpect(status().isCreated());
		Reserva reserva = reservaRepository.findAll().get(0);
		String alteracao = reservaJson(null, inicio, inicio.plusHours(1))
			.replace("\"inicio\"", "\"estado\":\"CANCELADA\",\"inicio\"");

		mockMvc.perform(put("/reservas/" + reserva.getId()).header("Authorization", autorizacao)
			.contentType(MediaType.APPLICATION_JSON).content(alteracao))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.estado").value("SOLICITADA"))
			.andExpect(jsonPath("$.solicitante.email").value("solicitante@teste.com"));
	}

	private String reservaJson(Long materialId) {
		LocalDateTime inicio = LocalDateTime.now().plusDays(30).withSecond(0).withNano(0);
		return reservaJson(materialId, inicio, inicio.plusHours(1));
	}

	private String reservaJson(Long materialId, LocalDateTime inicio, LocalDateTime fim) {
		String material = materialId == null ? "" : ",\"material\":{\"id\":" + materialId + "}";
		return "{\"inicio\":\"" + inicio + "\",\"fim\":\"" + fim + "\",\"sala\":{\"id\":"
			+ salaRepository.findAll().get(0).getId() + "},\"professor\":{\"id\":" + professorRepository.findAll().get(0).getId() + "}" + material + "}";
	}

	private String basic(String email) {
		String credentials = email + ":123";
		return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}
}