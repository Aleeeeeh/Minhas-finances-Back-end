package com.alefesilva.minhasfinancas.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.entity.Usuario;
import com.alefesilva.minhasfinancas.model.repository.UsuarioRepository;
import com.alefesilva.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService { ///@Service irá criar uma instância para quando precisar utilizar 
															///essa dependência
	private UsuarioRepository repository;
	///Autowride Conceito da injeção de dependência, funciona no atributo também, mas o mais indicado é no construtor
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository) {
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail.");
		}

	}

}
