package br.senac.organizacao.repository;

import br.senac.organizacao.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, Long> {
}