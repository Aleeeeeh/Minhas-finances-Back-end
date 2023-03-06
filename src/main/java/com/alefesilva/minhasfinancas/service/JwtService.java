package com.alefesilva.minhasfinancas.service;

import com.alefesilva.minhasfinancas.model.entity.Usuario;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

public interface JwtService {

	String gerarToken(Usuario usuario);
	// Claims são os playload ou seja os dados do usuário
	Claims obterClaims(String token) throws ExpiredJwtException;
	
	boolean isTokenValido(String token);
	
	String obterLoginUsuario(String token);
	
}
