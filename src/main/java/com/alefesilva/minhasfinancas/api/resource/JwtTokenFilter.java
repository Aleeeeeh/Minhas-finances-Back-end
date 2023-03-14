package com.alefesilva.minhasfinancas.api.resource;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.alefesilva.minhasfinancas.service.JwtService;
import com.alefesilva.minhasfinancas.service.impl.SecurityUserDetailsService;

public class JwtTokenFilter extends OncePerRequestFilter{
	
	private JwtService jwtService;
	
	private SecurityUserDetailsService userDetailService;
	
	public JwtTokenFilter(JwtService jwtService, SecurityUserDetailsService userDetailService) {
		this.jwtService = jwtService;
		this.userDetailService = userDetailService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
										throws ServletException, IOException {
		
	// Vamos mandar o nome Authorization no HEADER
	String authorization = request.getHeader("Authorization");
		
	/*Verifica se começa com Bearer pois o token é desse tipo, e ao mandar o token sempre terá esse nome no inicio
	E ficará assim Bearer + token */
	if(authorization != null && authorization.startsWith("Bearer")) {
		String token = authorization.split(" ")[1]; // 1° posição é o nome Bearer e a 2° é o token em si
		boolean isTokenValid = jwtService.isTokenValido(token); // Valida se o token é valido
		
		if(isTokenValid) {
			String login = jwtService.obterLoginUsuario(token);
			UserDetails usuarioAutenticado = userDetailService.loadUserByUsername(login);
			UsernamePasswordAuthenticationToken user =
					new UsernamePasswordAuthenticationToken(usuarioAutenticado, null, usuarioAutenticado.getAuthorities()); //getAuthorities são as permissões de usuário
			
			// Criando autenticação para jogar dentro do spring security
			user.setDetails( new WebAuthenticationDetailsSource().buildDetails(request));
			
			//Pegando contexto do Spring security e jogando nessa autenticação
			SecurityContextHolder.getContext().setAuthentication(user);
			
		}
	}
	
	//Depois de interceptar a requisição dar continuidade na execução
	filterChain.doFilter(request, response);
	
	}

}
