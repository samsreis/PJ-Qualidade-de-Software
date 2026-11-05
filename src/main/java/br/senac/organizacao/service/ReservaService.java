package br.senac.organizacao.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

import br.senac.organizacao.domain.EstadoReserva;
import br.senac.organizacao.domain.Material;
import br.senac.organizacao.domain.Perfil;
import br.senac.organizacao.domain.Reserva;
import br.senac.organizacao.domain.Usuario;
import br.senac.organizacao.repository.MaterialRepository;
import br.senac.organizacao.repository.ReservaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

	@Transactional
	public synchronized Reserva criar(Reserva r) {
		validarIntervalo(r);
		validarDisponibilidade(r, null);
		r.setMaterial(validarMaterial(r, r.getSolicitante(), null));
		r.setEstado(EstadoReserva.SOLICITADA);
		return reservaRepository.save(r);
	}

	@Transactional
	public synchronized Reserva atualizar(Reserva atual, Reserva alteracao) {
		validarIntervalo(alteracao);
		validarDisponibilidade(alteracao, atual.getId());
		alteracao.setMaterial(validarMaterial(alteracao, atual.getSolicitante(), atual.getId()));

		atual.setInicio(alteracao.getInicio());
		atual.setFim(alteracao.getFim());
		atual.setQuantidadeMaterial(alteracao.getQuantidadeMaterial());
		atual.setSala(alteracao.getSala());
		atual.setProfessor(alteracao.getProfessor());
		atual.setMaterial(alteracao.getMaterial());
		return reservaRepository.save(atual);
	}

	private void validarIntervalo(Reserva reserva) {
		if (reserva.getInicio() == null || reserva.getFim() == null || !reserva.getFim().isAfter(reserva.getInicio())
			|| reserva.getQuantidadeMaterial() < 1) {
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
	}

	private Material validarMaterial(Reserva reserva, Usuario solicitante, Long reservaIdIgnorada) {
		if (reserva.getMaterial() == null) return null;
		var material = materialRepository.findByIdForUpdate(reserva.getMaterial().getId())
			.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		if (material.isRestrito() && solicitante.getPerfil() != Perfil.RESPONSAVEL && solicitante.getPerfil() != Perfil.ADMIN) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN);
		}
		validarCapacidadeMaterial(reserva, material, reservaIdIgnorada);
		return material;
	}

	private void validarCapacidadeMaterial(Reserva reserva, Material material, Long reservaIdIgnorada) {
		List<Reserva> sobrepostas = reservaRepository.findReservasAtivasPorMaterialNoIntervalo(material.getId(),
			ATIVAS, reserva.getFim(), reserva.getInicio(), reservaIdIgnorada);
		TreeMap<LocalDateTime, Long> alteracoesDeEstoque = new TreeMap<>();
		for (Reserva existente : sobrepostas) {
			LocalDateTime inicioSobreposto = existente.getInicio().isAfter(reserva.getInicio())
				? existente.getInicio() : reserva.getInicio();
			LocalDateTime fimSobreposto = existente.getFim().isBefore(reserva.getFim())
				? existente.getFim() : reserva.getFim();
			alteracoesDeEstoque.merge(inicioSobreposto, (long) existente.getQuantidadeMaterial(), Long::sum);
			alteracoesDeEstoque.merge(fimSobreposto, -(long) existente.getQuantidadeMaterial(), Long::sum);
		}

		long unidadesEmUso = reserva.getQuantidadeMaterial();
		long picoDeUso = unidadesEmUso;
		for (long alteracao : alteracoesDeEstoque.values()) {
			unidadesEmUso += alteracao;
			picoDeUso = Math.max(picoDeUso, unidadesEmUso);
		}
		if (picoDeUso > material.getQuantidade()) throw new ResponseStatusException(HttpStatus.CONFLICT);
	}
}