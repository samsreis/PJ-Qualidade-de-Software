package br.senac.organizacao.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class RecursoSegurancaApiTest {
	@Autowired
	MockMvc mockMvc;

	@Test
	void listarSalasSemAutenticacaoRetorna401() throws Exception {
		mockMvc.perform(get("/api/salas"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void solicitanteNaoPodeCriarSala() throws Exception {
		mockMvc.perform(post("/api/salas").header("Authorization", basic("solicitante@teste.com"))
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"nome\":\"Sala teste\",\"capacidade\":20,\"localizacao\":\"Bloco C\",\"tipo\":\"Aula\"}"))
			.andExpect(status().isForbidden());
	}

	@Test
	void administradorPodeCriarSala() throws Exception {
		mockMvc.perform(post("/api/salas").header("Authorization", basic("admin@teste.com"))
			.contentType(MediaType.APPLICATION_JSON)
			.content("{\"nome\":\"Sala teste\",\"capacidade\":20,\"localizacao\":\"Bloco C\",\"tipo\":\"Aula\"}"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").isNumber());
	}

	private String basic(String email) {
		String credentials = email + ":123";
		return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
	}
}
