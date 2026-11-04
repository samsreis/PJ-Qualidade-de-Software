package br.senac.organizacao.controller;

import java.util.List;

import br.senac.organizacao.domain.EstadoReserva;
import br.senac.organizacao.domain.Reserva;
import br.senac.organizacao.repository.ReservaRepository;
import br.senac.organizacao.repository.UsuarioRepository;
import br.senac.organizacao.service.ReservaService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/reservas")
public class ReservaController {
	private final ReservaRepository reservaRepository;
	private final UsuarioRepository usuarioRepository;
	private final ReservaService reservaService;

	public ReservaController(ReservaRepository reservaRepository, UsuarioRepository usuarioRepository, ReservaService reservaService) {
		this.reservaRepository = reservaRepository;
		this.usuarioRepository = usuarioRepository;
		this.reservaService = reservaService;
	}

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Reserva criar(@RequestBody Reserva reserva) {
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		reserva.setSolicitante(usuarioRepository.findByEmail(email).orElseThrow());
		return reservaService.criar(reserva);
	}

	@GetMapping
	public List<Reserva> listar() {
		return reservaRepository.findAll();
	}

	@PutMapping("/{id}")
	public Reserva atualizar(@PathVariable Long id, @RequestBody Reserva reserva) {
		Reserva atual = dono(id);
		reserva.setId(id);
		reserva.setSolicitante(atual.getSolicitante());
		return reservaRepository.save(reserva);
	}

	@DeleteMapping("/{id}")
	public void excluir(@PathVariable Long id) {
		Reserva reserva = dono(id);
		if (reserva.getEstado() != EstadoReserva.SOLICITADA && reserva.getEstado() != EstadoReserva.APROVADA) throw new ResponseStatusException(HttpStatus.CONFLICT);
		reservaRepository.delete(reserva);
	}

	private Reserva dono(Long id) {
		Reserva reserva = reservaRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		String email = SecurityContextHolder.getContext().getAuthentication().getName();
		if (!reserva.getSolicitante().getEmail().equals(email)) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		return reserva;
	}
}