package br.senac.organizacao.service;

import java.util.List;

import br.senac.organizacao.domain.EstadoReserva;
import br.senac.organizacao.domain.Perfil;
import br.senac.organizacao.domain.Reserva;
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
		if (!r.getFim().isAfter(r.getInicio())) throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		if (r.getSala() != null && reservaRepository.existsBySala_IdAndEstadoInAndInicioBeforeAndFimAfter(r.getSala().getId(), ATIVAS, r.getFim(), r.getInicio())) throw new ResponseStatusException(HttpStatus.CONFLICT);
		if (r.getProfessor() != null && reservaRepository.existsByProfessor_IdAndEstadoInAndInicioBeforeAndFimAfter(r.getProfessor().getId(), ATIVAS, r.getFim(), r.getInicio())) throw new ResponseStatusException(HttpStatus.CONFLICT);
		if (r.getMaterial() != null && reservaRepository.existsByMaterial_IdAndEstadoInAndInicioBeforeAndFimAfter(r.getMaterial().getId(), ATIVAS, r.getFim(), r.getInicio())) throw new ResponseStatusException(HttpStatus.CONFLICT);
		if (r.getMaterial() != null) {
			var material = materialRepository.findById(r.getMaterial().getId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
			if (material.isRestrito() && r.getSolicitante().getPerfil() != Perfil.RESPONSAVEL && r.getSolicitante().getPerfil() != Perfil.ADMIN) throw new ResponseStatusException(HttpStatus.FORBIDDEN);
			r.setMaterial(material);
		}
		r.setEstado(EstadoReserva.SOLICITADA);
		return reservaRepository.save(r);
	}
}