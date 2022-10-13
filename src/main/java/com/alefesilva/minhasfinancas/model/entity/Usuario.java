package com.alefesilva.minhasfinancas.model.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "usuario", schema = "financas")
@Setter
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario { /// NoArgs (Cria Construtor sem argumento) / AllArgs (Cria Contrutor com
						/// argumentos)
	/// Podemos colocar @Data que já vem com todas as propriedades também

	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "nome")
	private String nome;

	@Column(name = "email")
	private String email;

	@Column(name = "senha")
	private String senha;

}
