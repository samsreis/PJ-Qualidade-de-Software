package br.senac.organizacao.service;

import java.util.List;

import br.senac.organizacao.domain.EstadoReserva;
import br.senac.organizacao.domain.Perfil;
import br.senac.organizacao.domain.Reserva;
import br.senac.organizacao.domain.Usuario;
import br.senac.organizacao.repository.MaterialRepository;
import br.senac.organizacao.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReservaService {
	private static final List<EstadoReserva> ATIVAS = List.of(EstadoReserva.SOLICITADA, EstadoReserva.APROVADA, EstadoReserva.EM_USO);
	private final ReservaRepository reservaRepository;
	private final MaterialRepository materialRepository;

	public ReservaService(ReservaRepository reservaRepository, MaterialRepository materialRepository) {
		this.reservaRepository = reservaRepository;
		this.materialRepository = materialRepository;
	}

	public synchronized Reserva criar(Reserva r) {
		validarIntervalo(r);
		validarDisponibilidade(r, null);
		validarMaterial(r);
		r.setEstado(EstadoReserva.SOLICITADA);
		return reservaRepository.save(r);
	}

	public synchronized Reserva atualizar(Reserva atual, Reserva alteracao) {
		validarIntervalo(alteracao);
		validarDisponibilidade(alteracao, atual.getId());
		validarMaterial(alteracao, atual.getSolicitante());

		atual.setInicio(alteracao.getInicio());
		atual.setFim(alteracao.getFim());
		atual.setSala(alteracao.getSala());
		atual.setProfessor(alteracao.getProfessor());
		atual.setMaterial(alteracao.getMaterial());
		return reservaRepository.save(atual);
	}

	private void validarIntervalo(Reserva reserva) {
		if (reserva.getInicio() == null || reserva.getFim() == null || !reserva.getFim().isAfter(reserva.getInicio())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
	}

	private void validarDisponibilidade(Reserva reserva, Long reservaIdIgnorada) {
		if (reserva.getSala() != null) {
			boolean conflita = reservaIdIgnorada == null
				? reservaRepository.existsBySala_IdAndEstadoInAndInicioBeforeAndFimAfter(reserva.getSala().getId(), ATIVAS, reserva.getFim(), reserva.getInicio())
				: reservaRepository.existsBySala_IdAndIdNotAndEstadoInAndInicioBeforeAndFimAfter(reserva.getSala().getId(), reservaIdIgnorada, ATIVAS, reserva.getFim(), reserva.getInicio());
			if (conflita) throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
		if (reserva.getProfessor() != null) {
			boolean conflita = reservaIdIgnorada == null
				? reservaRepository.existsByProfessor_IdAndEstadoInAndInicioBeforeAndFimAfter(reserva.getProfessor().getId(), ATIVAS, reserva.getFim(), reserva.getInicio())
				: reservaRepository.existsByProfessor_IdAndIdNotAndEstadoInAndInicioBeforeAndFimAfter(reserva.getProfessor().getId(), reservaIdIgnorada, ATIVAS, reserva.getFim(), reserva.getInicio());
			if (conflita) throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
		if (reserva.getMaterial() != null) {
			boolean conflita = reservaIdIgnorada == null
				? reservaRepository.existsByMaterial_IdAndEstadoInAndInicioBeforeAndFimAfter(reserva.getMaterial().getId(), ATIVAS, reserva.getFim(), reserva.getInicio())
				: reservaRepository.existsByMaterial_IdAndIdNotAndEstadoInAndInicioBeforeAndFimAfter(reserva.getMaterial().getId(), reservaIdIgnorada, ATIVAS, reserva.getFim(), reserva.getInicio());
			if (conflita) throw new ResponseStatusException(HttpStatus.CONFLICT);
		}
	}

	private void validarMaterial(Reserva reserva) {
		validarMaterial(reserva, reserva.getSolicitante());
	}

	private void validarMaterial(Reserva reserva, Usuario solicitante) {
		if (reserva.getMaterial() == null) return;
		var material = materialRepository.findById(reserva.getMaterial().getId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (material.isRestrito() && solicitante.getPerfil() != Perfil.RESPONSAVEL && solicitante.getPerfil() != Perfil.ADMIN) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		reserva.setMaterial(material);
	}
}