package com.alefesilva.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.alefesilva.minhasfinancas.model.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{
	
	boolean existsByEmail(String email);
	
	/* findBy no spring conhecido como Query methods, uma das formas do Spring de realizar consultas na tabela
	 Se tiver o dado ele retorna senão retorna vazio
	Optional<Usuario> findByEmail(String email);
	
	Se quiser fazer um AND no Sql, seria conforme exemplo:
	Optional<Usuario> findByEmailAndNome(String email, String nome); **Lembrando que nome e email está na tabela Usuario */
}
