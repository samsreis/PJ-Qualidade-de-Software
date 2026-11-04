package br.senac.organizacao;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

import br.senac.organizacao.domain.Material;
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
			.andExpect(status().isCreated());
	}

	private String reservaJson(Long materialId) {
		LocalDateTime inicio = LocalDateTime.now().plusDays(30).withSecond(0).withNano(0);
		String material = materialId == null ? "" : ",\"material\":{\"id\":" + materialId + "}";
		return "{\"inicio\":\"" + inicio + "\",\"fim\":\"" + inicio.plusHours(1) + "\",\"sala\":{\"id\":"
			+ salaRepository.findAll().get(0).getId() + "},\"professor\":{\"id\":" + professorRepository.findAll().get(0).getId() + "}" + material + "}";
	}

	private String basic(String email) {
		String credentials = email + ":123";
		return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}
}