package br.senac.organizacao.security;

import br.senac.organizacao.repository.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
			.headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/h2-console", "/h2-console/**").permitAll()
				.requestMatchers(HttpMethod.GET, "/salas/**", "/api/salas/**", "/professores/**", "/api/professores/**", "/materiais/**", "/api/materiais/**").authenticated()
				.requestMatchers(HttpMethod.POST, "/salas/**", "/api/salas/**", "/professores/**", "/api/professores/**", "/materiais/**", "/api/materiais/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.PUT, "/salas/**", "/api/salas/**", "/professores/**", "/api/professores/**", "/materiais/**", "/api/materiais/**").hasRole("ADMIN")
				.requestMatchers(HttpMethod.DELETE, "/salas/**", "/api/salas/**", "/professores/**", "/api/professores/**", "/materiais/**", "/api/materiais/**").hasRole("ADMIN")
				.requestMatchers("/reservas/**").authenticated()
				.anyRequest().authenticated())
			.httpBasic(Customizer.withDefaults());
		return http.build();
	}

	@Bean
	UserDetailsService userDetailsService(UsuarioRepository repository) {
		return email -> repository.findByEmail(email)
			.map(usuario -> User.withUsername(usuario.getEmail())
				.password(usuario.getSenha())
				.roles(usuario.getPerfil().name())
				.build())
			.orElseThrow(() -> new UsernameNotFoundException(email));
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}