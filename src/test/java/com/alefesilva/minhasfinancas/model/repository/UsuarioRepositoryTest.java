package com.alefesilva.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alefesilva.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test") ///Para pegar o arquivo properties de teste(Com as config do banco h2)
@DataJpaTest ///Ela grava os dados na base de dados em memória e após o teste ela deleta os dados, ou seja ela da um rollback
@AutoConfigureTestDatabase(replace = Replace.NONE) /// Impede que nossa config de banco sejam sobrescritas de alguma forma
public class UsuarioRepositoryTest {

	private static final String EMAIL = "usuario@gmail.com";

	private static final String NOME = "usuarioTeste";
	
	private static final String SENHA = "senhaTeste";

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager; /// Classe o JPA responsável por fazer as operações(Inserir, deletar, atualizar...), utilizado apenas para teste

	@Test
	public void deveVerificarAExistenciaDeUmEmail() {

			// Cenário
			Usuario usuario = criarUsuario();
			entityManager.persist(usuario);
			
			// Ação/execução
			boolean result = repository.existsByEmail(EMAIL);
			
			// Verificação
			Assertions.assertThat(result).isTrue();

	}
	
	@Test
	public void deveRetornarFalsoQuandoNaoHouverUsuarioCadastradoComOEmail() {
		//ação
		boolean result = repository.existsByEmail(EMAIL);
		
		//verificação
		Assertions.assertThat(result).isFalse();
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//cenário
		Usuario usuario = criarUsuario();
		//ação
		Usuario usuarioSalvo = repository.save(usuario);
		
		//verifica se salvou de fato
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		//cenário
		Usuario usuario = criarUsuario();
		entityManager.persist(usuario);
		
		//verifica se usuário já existe
		Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		Assertions.assertThat(result.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornarVazioAoBuscarUsuarioPorEmailQuandoNaoExisteNaBase() {
		
		//Verificacao
		Optional<Usuario> result = repository.findByEmail(EMAIL);
		
		Assertions.assertThat(result.isPresent()).isFalse();
	}
	
	@Test
	public static Usuario criarUsuario() {
		return Usuario
				.builder()
				.nome(NOME)
				.email(EMAIL)
				.senha(SENHA)
				.build();
	}

}








