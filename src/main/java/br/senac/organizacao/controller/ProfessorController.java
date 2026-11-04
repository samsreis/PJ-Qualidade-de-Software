package br.senac.organizacao.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.senac.organizacao.domain.*;
import br.senac.organizacao.repository.*;

@RestController @RequestMapping("/api/professores")
public class ProfessorController {
	private final ProfessorRepository repository; public ProfessorController(ProfessorRepository repository) { this.repository = repository; }
	@GetMapping public List<Professor> listar() { return repository.findAll(); }
	@GetMapping("/{id}") public ResponseEntity<Professor> buscar(@PathVariable Long id) { return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
	@PostMapping public Professor criar(@RequestBody Professor professor) { return repository.save(professor); }
	@PutMapping("/{id}") public Professor atualizar(@PathVariable Long id, @RequestBody Professor professor) { professor.setId(id); return repository.save(professor); }
	@DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) { repository.deleteById(id); return ResponseEntity.noContent().build(); }
}