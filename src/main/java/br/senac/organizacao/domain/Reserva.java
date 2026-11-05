package br.senac.organizacao.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class Reserva {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Sala sala;

	@ManyToOne
	private Professor professor;

	@ManyToOne
	private Material material;

	private LocalDateTime inicio;
	private LocalDateTime fim;
	private int quantidadeMaterial = 1;

	@Enumerated(EnumType.STRING)
	private EstadoReserva estado;

	@ManyToOne
	private Usuario solicitante;
}