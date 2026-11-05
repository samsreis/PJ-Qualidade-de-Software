package br.senac.organizacao.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

import br.senac.organizacao.domain.EstadoReserva;
import br.senac.organizacao.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReservaRepository extends JpaRepository<Reserva, Long> {
	boolean existsBySala_IdAndEstadoInAndInicioBeforeAndFimAfter(Long salaId, Collection<EstadoReserva> estados, LocalDateTime fim, LocalDateTime inicio);
	boolean existsBySala_IdAndIdNotAndEstadoInAndInicioBeforeAndFimAfter(Long salaId, Long reservaId, Collection<EstadoReserva> estados, LocalDateTime fim, LocalDateTime inicio);
	boolean existsByProfessor_IdAndEstadoInAndInicioBeforeAndFimAfter(Long professorId, Collection<EstadoReserva> estados, LocalDateTime fim, LocalDateTime inicio);
	boolean existsByProfessor_IdAndIdNotAndEstadoInAndInicioBeforeAndFimAfter(Long professorId, Long reservaId, Collection<EstadoReserva> estados, LocalDateTime fim, LocalDateTime inicio);

	@Query("""
		select reserva from Reserva reserva
		where reserva.material.id = :materialId
		and reserva.estado in :estados
		and reserva.inicio < :fim
		and reserva.fim > :inicio
		and (:reservaId is null or reserva.id <> :reservaId)
		""")
	List<Reserva> findReservasAtivasPorMaterialNoIntervalo(@Param("materialId") Long materialId,
		@Param("estados") Collection<EstadoReserva> estados, @Param("fim") LocalDateTime fim,
		@Param("inicio") LocalDateTime inicio, @Param("reservaId") Long reservaId);
}