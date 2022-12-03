package com.alefesilva.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
import com.alefesilva.minhasfinancas.model.entity.Usuario;
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
	
	@Test
	public void deveAtualizarOStatusDeUmLancamento() {
		
		//Cenário
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);
		
		//Execução
		service.atualizarStatus(lancamento, novoStatus);
		
		//Verificação
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
		
	}
	
	@Test
	public void deveObterUmLancamentoPorId() {
		
		//Cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));
		
		//Execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//Verificação
		Assertions.assertThat(resultado.isPresent()).isTrue();
		
	}
	
	@Test
	public void deveRetornarVazioQuandoUmLancamentoNaoExistir() {
		
		//Cenário
		Long id = 1l;
		
		Lancamento lancamento = LancamentoRepositoryTest.criarLancamento();
		lancamento.setId(id);
		
		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());
		
		//Execução
		Optional<Lancamento> resultado = service.obterPorId(id);
		
		//Verificação
		Assertions.assertThat(resultado.isPresent()).isFalse();
		
	}
	
	@Test
	public void deveLancarErrosAoValidarUmLancamento() {
		
		Usuario idUsuario = new Usuario();
		idUsuario.setId(1l);
		
		//Instanciamos um lancamento vazio para começar as testar desde a primeira excessão desse método de validar
		Lancamento lancamento = new Lancamento();
		
		Throwable erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");
		
		lancamento.setDescricao("Salário");
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(null);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(0);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(13);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(12);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(null);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(2022);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(null);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");
		
		lancamento.setUsuario(idUsuario);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
	
		lancamento.setValor(BigDecimal.ZERO);
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.valueOf(1));
		
		erro = Assertions.catchThrowable( () -> service.validar(lancamento) );
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um tipo de lançamento.");
		
	}
	
}


















