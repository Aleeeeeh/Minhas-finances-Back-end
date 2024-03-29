package com.alefesilva.minhasfinancas.service;

import java.util.List;
import java.util.Optional;

import com.alefesilva.minhasfinancas.model.entity.Usuario;

public interface UsuarioService {
	Usuario autenticar(String email, String senha);
	
	Usuario salvarUsuario(Usuario usuario);
	
	void validarEmail(String email);
	
	Optional<Usuario> obterPorId(Long id);
	
	List<Usuario> retornaUsuariosCadastrados();
	
	Usuario atualizar(Usuario usuario);
}
