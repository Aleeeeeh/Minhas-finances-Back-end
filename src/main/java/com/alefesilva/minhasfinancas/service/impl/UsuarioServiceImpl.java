package com.alefesilva.minhasfinancas.service.impl;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	private PasswordEncoder encoder;
	
	///Autowride Conceito da injeção de dependência, funciona no atributo também, mas o mais indicado é no construtor
	@Autowired
	public UsuarioServiceImpl(UsuarioRepository repository, PasswordEncoder encoder) {
		this.repository = repository;
		this.encoder = encoder;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		//Se usuário não estiver presente na base de dados disparar a excessão
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não encontrado para o email informado.");
		}
		
		//Método matches ele valida senha digitada com senha criptografada do banco de bater retorna true
		boolean senhasConferem = encoder.matches(senha, usuario.get().getSenha());
		
		if(!senhasConferem) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		
		/*Isso de forma didática, mas na prática quando disparar o erro para no usuário colocar que e-mail ou senha
		é inválido, assim dando maior segurança.
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida.");
		}
		*/
		return usuario.get(); //usuario.get() -> Retorna a instância do usuário
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		criptografarSenha(usuario);
		
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail.");
		}

	}
	
	public void criptografarSenha(Usuario usuario) {
		String senha = usuario.getSenha();
		String senheCripto = encoder.encode(senha);
		usuario.setSenha(senheCripto);
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		return repository.findById(id);
	}

	@Override
	public List<Usuario> retornaUsuariosCadastrados() {
		return repository.findAll();
	}

}
