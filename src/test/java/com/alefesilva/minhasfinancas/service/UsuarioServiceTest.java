package com.alefesilva.minhasfinancas.service;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alefesilva.minhasfinancas.exception.ErroAutenticacao;
import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.entity.Usuario;
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

	@MockBean // Cria uma instância de UsuarioRepository alocada ao invés da real
	UsuarioRepository repository;
	
	@BeforeEach // Antes de executar o restante do código
	public void setUp() {
		// Mocks -> Para simular instância de classes e testar métodos
		service = new UsuarioServiceImpl(repository);
	}
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
			//cenário
			String email = "emailTeste@gmail.com";
			String senha = "Senhateste";
			
			Usuario usuario = Usuario.builder().email(email).senha(senha).id(1l).build();
			Mockito.when( repository.findByEmail(email)).thenReturn(Optional.of(usuario));
			
			//ação
			Usuario result = service.autenticar(email, senha);
			
			//verificação
			org.assertj.core.api.Assertions.assertThat(result).isNotNull();
	}
	
	@Test
	public void deveLancarErroQuandoNaoEncontrarUsuarioCadastradoComOEmailInformado() {
		Assertions.assertThrows(ErroAutenticacao.class, () ->{
			//cenário anyString indica ser um valor qualquer
			Mockito.when( repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
			
			//ação
			service.autenticar("emailTeste@gmail.com", "Senhateste");
		});
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		Assertions.assertThrows(ErroAutenticacao.class, () ->{
			//cenário
			String email = "email@teste.com";
			String senha = "SenhaAtual";
			Usuario usuario = Usuario.builder().email(email).senha(senha).build();
			Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
			
			//ação
			service.autenticar(email, "OutraSenha");
		});	
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
