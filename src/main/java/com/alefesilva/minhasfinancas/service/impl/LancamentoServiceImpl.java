package com.alefesilva.minhasfinancas.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.entity.Lancamento;
import com.alefesilva.minhasfinancas.model.enums.StatusLancamento;
import com.alefesilva.minhasfinancas.model.enums.TipoLancamento;
import com.alefesilva.minhasfinancas.model.repository.LancamentoRepository;
import com.alefesilva.minhasfinancas.service.LancamentoService;


@Service  ///Para garantir que aconteça a injeção de dependência
public class LancamentoServiceImpl implements LancamentoService{
	
	private LancamentoRepository repository;
	
	public LancamentoServiceImpl(LancamentoRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public Lancamento salvar(Lancamento lancamento) {
		validar(lancamento);
		lancamento.setStatus(StatusLancamento.PENDENTE); // Na regra de negócio quando reaizamos um lançamento ele vem nesse status
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public Lancamento atualizar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId()); // Para garantir que irá passar um lanc com ID
		validar(lancamento);
		return repository.save(lancamento);
	}

	@Override
	@Transactional
	public void deletar(Lancamento lancamento) {
		Objects.requireNonNull(lancamento.getId());
		repository.delete(lancamento);
	}

	@Override
	@Transactional(readOnly = true) // Apenas leitura
	public List<Lancamento> buscar(Lancamento lancamentoFiltro) {
		//withIgnoreCase() -> Não leva em conta o case sensitive
		//withStringMatcher() -> Usamos para buscar como um like do SQL, porém a diferença é que temos
		//o starting que busca pelo inicio da String, end no final e assim por diante. No exemplo
		//utilizamos o que busca a String como um todo.
		
		Example<Lancamento> example = Example.of(lancamentoFiltro,
				ExampleMatcher.matching()
				.withIgnoreCase()
				.withIgnoreNullValues()
				.withIgnorePaths("id","descricao","tipo","valor","dataCadastro","status")
				.withStringMatcher(StringMatcher.CONTAINING)); //Funciona como um like
		
		return repository.findAll(example);
	}

	@Override
	public void atualizarStatus(Lancamento lancamento, StatusLancamento status) {
		lancamento.setStatus(status);
		atualizar(lancamento);
	}

	@Override
	public void validar(Lancamento lancamento) {
		
		if(lancamento.getDescricao() == null || lancamento.getDescricao().trim().equals("")) {
			throw new RegraNegocioException("Informe uma Descrição válida.");
		}
		
		if(lancamento.getMes() == null || lancamento.getMes() < 1 || lancamento.getMes() > 12) {
			throw new RegraNegocioException("Informe um Mês válido.");
		}
		
		if(lancamento.getAno() == null || lancamento.getAno().toString().length() != 4) {
			throw new RegraNegocioException("Informe um Ano válido.");
		}
		
		//lancamento.getUsuario().getId() != null -> Verificar
		if(lancamento.getUsuario() == null) {
			throw new RegraNegocioException("Informe um Usuário.");
		}
		
		//compareTo -> Compara o valor que recebemos por parâmetro com valor do banco, e a condição é a seguinte:
		// 1 Se for maior que o valor recebido
		// 0 Se caso o valor seja exatamente igual
		// -1 Se for menor do que o valor passado 
		if(lancamento.getValor() == null || lancamento.getValor().compareTo(BigDecimal.ZERO) < 1) {
			throw new RegraNegocioException("Informe um Valor válido.");
		}
		
		if(lancamento.getTipo() == null) {
			throw new RegraNegocioException("Informe um tipo de lançamento.");
		}
		
	}

	@Override
	public Optional<Lancamento> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	@Transactional(readOnly = true) // Somente leitura
	public BigDecimal obterSaldoPorUsuario(Long id) {
		BigDecimal receitas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO);
		BigDecimal despesas = repository.obterSaldoPorTipoLancamentoEUsuarioEStatus(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO);
		
		if(receitas == null) {
			receitas = BigDecimal.ZERO;
		}
		
		if(despesas == null) {
			despesas = BigDecimal.ZERO;
		}
		
		return receitas.subtract(despesas);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Lancamento> buscarLancamentosPeriodo(Long id, Integer mesAtual, Integer mesFinal, Integer anoAtual, 
													Integer anoFinal){
		return repository.obterLancamentosPorPeriodoEUsuario(id, mesAtual, mesFinal, anoAtual, anoFinal);
	}

}






