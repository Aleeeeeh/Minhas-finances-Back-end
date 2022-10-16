package com.alefesilva.minhasfinancas.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.entity.Usuario;
import com.alefesilva.minhasfinancas.model.repository.UsuarioRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	private static final String EMAIL = "emailTeste@gmail.com";

	private static final String NOME = "usuarioTeste";

	@Autowired
	UsuarioService service;

	@Autowired
	UsuarioRepository repository;

	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> { // Para não disparar um excessão
			// cenario
			repository.deleteAll();

			// ação
			service.validarEmail(EMAIL);
		});
	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> { // Dispara Excessão se email já existir
			// cenário
			Usuario usuario = Usuario.builder().nome(NOME).email(EMAIL).build();
			repository.save(usuario);

			// ação
			service.validarEmail(EMAIL);
		});

	}

}
