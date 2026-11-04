package br.senac.organizacao.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.senac.organizacao.domain.*;
import br.senac.organizacao.repository.*;

@RestController @RequestMapping("/api/salas")
public class SalaController {
	private final SalaRepository repository; public SalaController(SalaRepository repository) { this.repository = repository; }
	@GetMapping public List<Sala> listar() { return repository.findAll(); }
	@GetMapping("/{id}") public ResponseEntity<Sala> buscar(@PathVariable Long id) { return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
	@PostMapping public Sala criar(@RequestBody Sala sala) { return repository.save(sala); }
	@PutMapping("/{id}") public Sala atualizar(@PathVariable Long id, @RequestBody Sala sala) { sala.setId(id); return repository.save(sala); }
	@DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) { repository.deleteById(id); return ResponseEntity.noContent().build(); }
}