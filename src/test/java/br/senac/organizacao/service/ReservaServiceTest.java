package br.senac.organizacao.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;

import br.senac.organizacao.domain.Material;
import br.senac.organizacao.domain.Professor;
import br.senac.organizacao.domain.Reserva;
import br.senac.organizacao.domain.Sala;
import br.senac.organizacao.repository.MaterialRepository;
import br.senac.organizacao.repository.ReservaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {
	@Mock
	ReservaRepository reservaRepository;
	@Mock
	MaterialRepository materialRepository;
	@InjectMocks
	ReservaService reservaService;

	@Test
	void fimAntesDoInicioDeveLancarErro() {
		Reserva reserva = new Reserva();
		reserva.setInicio(LocalDateTime.of(2026, 12, 1, 11, 0));
		reserva.setFim(LocalDateTime.of(2026, 12, 1, 10, 0));

		ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> reservaService.criar(reserva));

		assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
	}

	@Test
	void sobreposicaoDeSalaDeveLancarConflito() {
		LocalDateTime inicio = LocalDateTime.of(2026, 12, 1, 10, 0);
		LocalDateTime fim = inicio.plusHours(1);
		Sala sala = new Sala();
		sala.setId(1L);
		Reserva reserva = new Reserva();
		reserva.setInicio(inicio);
		reserva.setFim(fim);
		reserva.setSala(sala);
		when(reservaRepository.existsBySala_IdAndEstadoInAndInicioBeforeAndFimAfter(eq(1L), anyCollection(), eq(fim), eq(inicio))).thenReturn(true);

		ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> reservaService.criar(reserva));

		assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
	}

	@Test
	void sobreposicaoDeProfessorDeveLancarConflito() {
		LocalDateTime inicio = LocalDateTime.of(2026, 12, 1, 10, 0);
		LocalDateTime fim = inicio.plusHours(1);
		Professor professor = new Professor();
		professor.setId(2L);
		Reserva reserva = new Reserva();
		reserva.setInicio(inicio);
		reserva.setFim(fim);
		reserva.setProfessor(professor);
		when(reservaRepository.existsByProfessor_IdAndEstadoInAndInicioBeforeAndFimAfter(eq(2L), anyCollection(), eq(fim), eq(inicio))).thenReturn(true);

		ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> reservaService.criar(reserva));

		assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
	}

	@Test
	void sobreposicaoDeMaterialDeveLancarConflito() {
		LocalDateTime inicio = LocalDateTime.of(2026, 12, 1, 10, 0);
		LocalDateTime fim = inicio.plusHours(1);
		Material material = new Material();
		material.setId(3L);
		Reserva reserva = new Reserva();
		reserva.setInicio(inicio);
		reserva.setFim(fim);
		reserva.setMaterial(material);
		when(reservaRepository.existsByMaterial_IdAndEstadoInAndInicioBeforeAndFimAfter(eq(3L), anyCollection(), eq(fim), eq(inicio))).thenReturn(true);

		ResponseStatusException erro = assertThrows(ResponseStatusException.class, () -> reservaService.criar(reserva));

		assertEquals(HttpStatus.CONFLICT, erro.getStatusCode());
	}
}