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
import org.springframework.boot.test.mock.mockito.SpyBean;
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

	@SpyBean
	UsuarioServiceImpl service;

	@MockBean // Cria uma instância de UsuarioRepository alocada ao invés da real
	UsuarioRepository repository;
	
	@Test
	public void deveSalvarUmUsuario() {
		Assertions.assertDoesNotThrow(() ->{
			//cenário **doNothing -> não faça nada
			Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
			Usuario usuario = Usuario.builder()
					.id(1l)
					.nome("Teste")
					.email("Email@Teste.com")
					.senha("Senha de teste")
					.build();
			
			Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
			
			//ação
			Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
			
			//verificação
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo).isNotNull();
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("Teste");
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("Email@Teste.com");
			org.assertj.core.api.Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("Senha de teste");
		});
	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailJaCadastrado() {
		Assertions.assertThrows(RegraNegocioException.class, () -> {
			//cenário
			String email = "email2022@teste.com";
			Usuario usuario = Usuario.builder().email(email).build();
			//Dispara excessão
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
			
			//ação
			service.salvarUsuario(usuario);
			
			//verificação Mockito.verify(nosso mock, e qntd de vzs que chama o método).nunca Chame o método de salvar user ,
			
			Mockito.verify(repository, Mockito.never()).save(usuario);
		});
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
		//cenário anyString indica ser um valor qualquer
		Mockito.when( repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//ação
		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.autenticar("emailTeste@gmail.com", "Senhateste"));
		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não encontrado para o email informado.");
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		//cenário
		String email = "email@teste.com";
		String senha = "SenhaAtual";
		Usuario usuario = Usuario.builder().email(email).senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//ação
		Throwable exception = org.assertj.core.api.Assertions.catchThrowable(() -> service.autenticar(email, "OutraSenha") );
		org.assertj.core.api.Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida.");
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
