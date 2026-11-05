package br.senac.organizacao.repository;

import java.util.Optional;

import br.senac.organizacao.domain.Material;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MaterialRepository extends JpaRepository<Material, Long> {
	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("select material from Material material where material.id = :id")
	Optional<Material> findByIdForUpdate(@Param("id") Long id);
}