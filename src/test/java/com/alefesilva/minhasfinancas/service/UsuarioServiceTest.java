package com.alefesilva.minhasfinancas.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.repository.UsuarioRepository;
import com.alefesilva.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	private static final String EMAIL = "emailTeste@gmail.com";

	//private static final String NOME = "usuarioTeste";

	@Autowired
	UsuarioService service;

	@Autowired
	UsuarioRepository repository;
	
	@BeforeEach // Antes de executar o restante do código
	public void setUp() {
		// Mocks -> Para simular instância de classes e testar métodos
		repository = Mockito.mock(UsuarioRepository.class);
		service = new UsuarioServiceImpl(repository);
	}

	@Test
	public void deveValidarEmail() {
		Assertions.assertDoesNotThrow(() -> { // Para não disparar um excessão
			// cenario
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);

			// ação
			service.validarEmail(EMAIL);
		});
	}

	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> { // Dispara Excessão se email já existir
			// cenário
			Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);

			// ação
			service.validarEmail(EMAIL);
		});

	}

}
