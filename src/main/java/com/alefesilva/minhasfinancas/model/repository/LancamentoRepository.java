package com.alefesilva.minhasfinancas.model.repository;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.alefesilva.minhasfinancas.model.entity.Lancamento;
import com.alefesilva.minhasfinancas.model.enums.StatusLancamento;
import com.alefesilva.minhasfinancas.model.enums.TipoLancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{
	//Não é necessário a utilização do ON tabelaUsuário, pois o hibernate pegando a chave estrangeira usuário já faz a referência.
	//JPQL -> Linguagem usada para realizar a consulta abaixo. é uma linguagem de consulta orientada a objeto independente de 
	//plataforma definida como parte da especificação Jakarta Persistence. O JPQL é usado para fazer consultas em entidades 
	//armazenadas em um banco de dados relacional.
	
	@Query( value = 
			"select sum(l.valor) from Lancamento l join l.usuario u "
		  + "where u.id = :idUsuario and l.tipo = :tipo and l.status = :status group by u")
	BigDecimal obterSaldoPorTipoLancamentoEUsuarioEStatus( 
			@Param("idUsuario") Long idUsuario, 
			@Param("tipo") TipoLancamento tipo ,
			@Param("status") StatusLancamento status);
}
