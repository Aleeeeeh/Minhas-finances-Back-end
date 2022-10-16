package com.alefesilva.minhasfinancas.service.impl;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alefesilva.minhasfinancas.exception.ErroAutenticacao;
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
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		//Se usuário não estiver presente na base de dados disparar a excessão
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}
		//Isso de forma didática, mas na prática quando disparar o erro para no usuário colocar que e-mail ou senha
		//é inválido, assim dando maior segurança.
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		return usuario.get(); //usuario.get() -> Retorna a instância do usuário
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail.");
		}

	}

}
