package com.alefesilva.minhasfinancas.model.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alefesilva.minhasfinancas.model.entity.Usuario;
import com.alefesilva.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@SpringBootTest
public class UsuarioRepositoryTest {

	public static String EMAIL = "usuario@gmail.com";

	public static String NOME = "usuarioTeste";

	@Autowired
	UsuarioService service;

	@Autowired
	UsuarioRepository repository;

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {

			// Cenário
			Usuario usuario = Usuario.builder().nome(NOME).email(EMAIL).build();
			repository.save(usuario);
			
			// Ação/execução
			boolean result = repository.existsByEmail(EMAIL);
			
			// Verificação
			Assertions.assertThat(result).isTrue();

	}

}
