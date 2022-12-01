package com.alefesilva.minhasfinancas.service;

import java.util.Arrays;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.entity.Lancamento;
import com.alefesilva.minhasfinancas.model.enums.StatusLancamento;
import com.alefesilva.minhasfinancas.model.repository.LancamentoRepository;
import com.alefesilva.minhasfinancas.model.repository.LancamentoRepositoryTest;
import com.alefesilva.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {
	
	/*
	 * SpyBean -> Aponta para classe que estamos testando, irá chamar os métodos reais
	 * MockBean(Famoso Mock) -> Simular o comportamento da camada de repository, em resumo não irá gravar os dados na tabela
	 * real.
	 */

	@SpyBean
	LancamentoServiceImpl service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		//Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar); // Não faça nada quando chamar método validar
		
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//Execução
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//Verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void naoDeveSalvarUmLancamentoQuandoHouverErroDeValidacao() {
		
		//Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		Mockito.doThrow( RegraNegocioException.class ).when(service).validar(lancamentoASalvar);
		
		//Execução e verificação
		Assertions.catchThrowableOfType( () -> service.salvar(lancamentoASalvar), RegraNegocioException.class );
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar); // Garantir que não chegou no método de salvar
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		//Cenário	
		Lancamento lancamentoSalvo = LancamentoRepositoryTest.criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo); // Não faça nada quando chamar método validar
		
		//Salva o lançamento de fato
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoSalvo);
		
		//Execução (Testa o atualizar)
		service.atualizar(lancamentoSalvo);
		
		//Verificação (Se está chegando até o método de salvar com esse ID gerado)
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoQueAindaNaoFoiSalvo() {
		
		//Cenário
		Lancamento lancamentoASalvar = LancamentoRepositoryTest.criarLancamento();
		
		//Execução e verificação
		Assertions.catchThrowableOfType( () -> service.atualizar(lancamentoASalvar), NullPointerException.class );
		Mockito.verify(repository, Mockito.never()).save(lancamentoASalvar); // Garantir que não chegou no método de salvar
		
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		//Execução
		service.deletar(lancamento);
		
		//Verificação (Se chegou até o método de deletar de fato)
		Mockito.verify( repository ).delete(lancamento);
		
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoQueAindaNaoFoiSalvo() {
		
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		
		//Execução(O método deletar quebra se não houver ID, nesse caso não estamos passando depois de criar a instancia)
		Assertions.catchThrowableOfType( () -> service.deletar(lancamento), NullPointerException.class );
		
		//Verificação(Se nunca chegou no método deletar de fato)
		Mockito.verify( repository, Mockito.never() ).delete(lancamento);
		
	}
	
	@Test
	public void deveFiltrarLancamentos() {
		
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		
		//Cria uma lista com um lancamento
		List<Lancamento> lista = Arrays.asList(lancamento);
		
		//Procura um lancamento do tipo exemple e retorna a lista
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		List<Lancamento> resultado = service.buscar(lancamento);
		
		//Verificações
		Assertions
		.assertThat(resultado)
		.isNotEmpty() //Não é vazio
		.hasSize(1) //Tem 1 item na lista
		.contains(lancamento); //contém 1 lançamento
		
	}
	
}








