package br.senac.organizacao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import br.senac.organizacao.domain.Reserva;
import br.senac.organizacao.domain.Sala;
import br.senac.organizacao.domain.Usuario;
import br.senac.organizacao.repository.ReservaRepository;
import br.senac.organizacao.repository.SalaRepository;
import br.senac.organizacao.repository.UsuarioRepository;
import br.senac.organizacao.service.ReservaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@SpringBootTest
@AutoConfigureMockMvc
class ConcorrenciaTest {
	@Autowired
	ReservaService reservaService;
	@Autowired
	ReservaRepository reservaRepository;
	@Autowired
	SalaRepository salaRepository;
	@Autowired
	UsuarioRepository usuarioRepository;

	@BeforeEach
	void limparReservas() {
		reservaRepository.deleteAll();
	}

	@Test
	void apenasUmaReservaConcorrenteDeveSerCriada() throws Exception {
		Sala sala = salaRepository.findAll().get(0);
		Usuario solicitante = usuarioRepository.findByEmail("solicitante@teste.com").orElseThrow();
		CountDownLatch prontas = new CountDownLatch(2);
		CountDownLatch iniciar = new CountDownLatch(1);
		List<Throwable> erros = Collections.synchronizedList(new java.util.ArrayList<>());
		Runnable tarefa = () -> {
			prontas.countDown();
			try {
				if (!iniciar.await(5, TimeUnit.SECONDS)) throw new IllegalStateException();
				reservaService.criar(novaReserva(sala, solicitante));
			} catch (Exception erro) {
				erros.add(erro);
			}
		};
		Thread primeira = new Thread(tarefa);
		Thread segunda = new Thread(tarefa);
		primeira.start();
		segunda.start();
		assertTrue(prontas.await(5, TimeUnit.SECONDS));
		iniciar.countDown();
		primeira.join(5000);
		segunda.join(5000);

		assertFalse(primeira.isAlive());
		assertFalse(segunda.isAlive());
		assertEquals(1, reservaRepository.count());
		assertEquals(1, erros.size());
		assertInstanceOf(ResponseStatusException.class, erros.get(0));
		assertEquals(HttpStatus.CONFLICT, ((ResponseStatusException) erros.get(0)).getStatusCode());
	}

	private Reserva novaReserva(Sala sala, Usuario solicitante) {
		LocalDateTime inicio = LocalDateTime.now().plusDays(10).withSecond(0).withNano(0);
		Reserva reserva = new Reserva();
		reserva.setSala(sala);
		reserva.setSolicitante(solicitante);
		reserva.setInicio(inicio);
		reserva.setFim(inicio.plusHours(1));
		return reserva;
	}
}