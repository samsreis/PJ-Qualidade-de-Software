package br.senac.organizacao.repository;

import java.time.LocalDateTime;
import java.util.Collection;

import br.senac.organizacao.domain.EstadoReserva;
import br.senac.organizacao.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
	boolean existsBySala_IdAndEstadoInAndInicioBeforeAndFimAfter(Long salaId, Collection<EstadoReserva> estados, LocalDateTime fim, LocalDateTime inicio);
	boolean existsByProfessor_IdAndEstadoInAndInicioBeforeAndFimAfter(Long professorId, Collection<EstadoReserva> estados, LocalDateTime fim, LocalDateTime inicio);
	boolean existsByMaterial_IdAndEstadoInAndInicioBeforeAndFimAfter(Long materialId, Collection<EstadoReserva> estados, LocalDateTime fim, LocalDateTime inicio);
}