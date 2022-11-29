package com.alefesilva.minhasfinancas.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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
	
}








