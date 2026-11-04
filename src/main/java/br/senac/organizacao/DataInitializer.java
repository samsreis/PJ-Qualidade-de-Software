package br.senac.organizacao;

import java.util.List;

import br.senac.organizacao.domain.Material;
import br.senac.organizacao.domain.Perfil;
import br.senac.organizacao.domain.Professor;
import br.senac.organizacao.domain.Sala;
import br.senac.organizacao.domain.Usuario;
import br.senac.organizacao.repository.MaterialRepository;
import br.senac.organizacao.repository.ProfessorRepository;
import br.senac.organizacao.repository.SalaRepository;
import br.senac.organizacao.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
	private final UsuarioRepository usuarioRepository;
	private final SalaRepository salaRepository;
	private final ProfessorRepository professorRepository;
	private final MaterialRepository materialRepository;
	private final PasswordEncoder passwordEncoder;

	public DataInitializer(UsuarioRepository usuarioRepository, SalaRepository salaRepository, ProfessorRepository professorRepository, MaterialRepository materialRepository, PasswordEncoder passwordEncoder) {
		this.usuarioRepository = usuarioRepository;
		this.salaRepository = salaRepository;
		this.professorRepository = professorRepository;
		this.materialRepository = materialRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		Usuario solicitante = new Usuario();
		solicitante.setNome("Solicitante");
		solicitante.setEmail("solicitante@teste.com");
		solicitante.setSenha(passwordEncoder.encode("123"));
		solicitante.setPerfil(Perfil.SOLICITANTE);
		Usuario responsavel = new Usuario();
		responsavel.setNome("Responsavel");
		responsavel.setEmail("responsavel@teste.com");
		responsavel.setSenha(passwordEncoder.encode("123"));
		responsavel.setPerfil(Perfil.RESPONSAVEL);
		Usuario admin = new Usuario();
		admin.setNome("Admin");
		admin.setEmail("admin@teste.com");
		admin.setSenha(passwordEncoder.encode("123"));
		admin.setPerfil(Perfil.ADMIN);
		usuarioRepository.saveAll(List.of(solicitante, responsavel, admin));

		Sala sala1 = new Sala();
		sala1.setNome("Sala 1");
		sala1.setCapacidade(30);
		sala1.setLocalizacao("Bloco A");
		sala1.setTipo("Aula");
		Sala sala2 = new Sala();
		sala2.setNome("Sala 2");
		sala2.setCapacidade(20);
		sala2.setLocalizacao("Bloco B");
		sala2.setTipo("Laboratorio");
		salaRepository.saveAll(List.of(sala1, sala2));

		Professor professor1 = new Professor();
		professor1.setNome("Professor 1");
		professor1.setEmail("professor1@senac.br");
		professor1.setCompetencia("Programacao");
		Professor professor2 = new Professor();
		professor2.setNome("Professor 2");
		professor2.setEmail("professor2@senac.br");
		professor2.setCompetencia("Redes");
		professorRepository.saveAll(List.of(professor1, professor2));

		Material material1 = new Material();
		material1.setNome("Projetor");
		material1.setTipo("Audiovisual");
		material1.setQuantidade(5);
		material1.setRestrito(true);
		Material material2 = new Material();
		material2.setNome("Cabo HDMI");
		material2.setTipo("Acessorio");
		material2.setQuantidade(10);
		material2.setRestrito(false);
		materialRepository.saveAll(List.of(material1, material2));
	}
}