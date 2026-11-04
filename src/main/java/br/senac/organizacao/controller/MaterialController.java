package br.senac.organizacao.controller;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import br.senac.organizacao.domain.*;
import br.senac.organizacao.repository.*;

@RestController @RequestMapping("/api/materiais")
public class MaterialController {
	private final MaterialRepository repository; public MaterialController(MaterialRepository repository) { this.repository = repository; }
	@GetMapping public List<Material> listar() { return repository.findAll(); }
	@GetMapping("/{id}") public ResponseEntity<Material> buscar(@PathVariable Long id) { return repository.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()); }
	@PostMapping public Material criar(@RequestBody Material material) { return repository.save(material); }
	@PutMapping("/{id}") public Material atualizar(@PathVariable Long id, @RequestBody Material material) { material.setId(id); return repository.save(material); }
	@DeleteMapping("/{id}") public ResponseEntity<Void> excluir(@PathVariable Long id) { repository.deleteById(id); return ResponseEntity.noContent().build(); }
}