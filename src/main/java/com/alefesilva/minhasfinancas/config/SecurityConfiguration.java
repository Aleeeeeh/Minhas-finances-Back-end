package com.alefesilva.minhasfinancas.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.alefesilva.minhasfinancas.service.impl.SecurityUserDetailsService;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private SecurityUserDetailsService userDetailsService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		/* Uma dos algoritmos de autenticaçao mais seguros de criptogafia do Spring Security.
		 * Ao contrário do MD5 que sempre cria o mesmo hash, o BCripy sempre cria um hash diferente para comparar.
		 * Bean é para colocar no contexto do Spring, nesse caso conseguimos utilizar esse em outros lugares
		 */
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {	
		// Com isso a autentição da API vai ser pelo email e senha do usuário
		auth
		.userDetailsService(userDetailsService)
		.passwordEncoder(passwordEncoder());
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		/*Em resumo as duas rotas podem receber requisiçoes sem precisar autenticar, as demais mantém sendo 
		ecessário passar a senha
		Obs: Em casos de perfil de acesso poderiamos usar propriedades como hasAnyRole('RH','ADM') e hasAuthority
		ou seja,ambos departamentos irão utilizar essa rota apenas*/
		http.csrf().disable()
		.authorizeRequests()      //Essas rotas já estarão autenticadas(autentica user e cadastro de user)
		.antMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll() 
		.antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
		.anyRequest().authenticated()
		.and()
		/*Quando usuário autentica na API grava na sessão e nas demais não pede mais autenticação, esse trecho faz com que 
		toda requisição seja obrigatório autenticar*/
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and()
		.httpBasic();
	}
	
}
