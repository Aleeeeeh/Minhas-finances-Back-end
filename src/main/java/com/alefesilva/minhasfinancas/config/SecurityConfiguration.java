package com.alefesilva.minhasfinancas.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import com.alefesilva.minhasfinancas.api.resource.JwtTokenFilter;
import com.alefesilva.minhasfinancas.service.JwtService;
import com.alefesilva.minhasfinancas.service.impl.SecurityUserDetailsService;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private SecurityUserDetailsService userDetailsService;
	
	@Autowired
	private JwtService jwtService;
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		/* Uma dos algoritmos de autenticaçao mais seguros de criptogafia do Spring Security.
		 * Ao contrário do MD5 que sempre cria o mesmo hash, o BCripy sempre cria um hash diferente para comparar.
		 * Bean é para colocar no contexto do Spring, nesse caso conseguimos utilizar esse em outros lugares
		 */
		PasswordEncoder encoder = new BCryptPasswordEncoder();
		return encoder;
	}
	
	@Bean
	public JwtTokenFilter jwtTokenFilter() {
		return new JwtTokenFilter(jwtService, userDetailsService);
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
		necessário passar a senha
		Obs: Em casos de perfil de acesso poderiamos usar propriedades como hasAnyRole('RH','ADM') e hasAuthority
		ou seja,ambos departamentos irão utilizar essa rota apenas*/
		http.csrf().disable()
		.authorizeRequests()      //Essas rotas já estarão autenticadas(autentica user e cadastro de user)
		.antMatchers(HttpMethod.POST, "/api/usuarios/autenticar").permitAll() 
		.antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
		.antMatchers("/h2-console/**").permitAll() 
		.anyRequest().authenticated()
		.and()
		/*Quando usuário autentica na API grava na sessão e nas demais não pede mais autenticação, esse trecho faz com que 
		toda requisição seja obrigatório autenticar*/
		.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
		.and().headers().frameOptions().sameOrigin() //Libera a interface do h2-console
		.and()
		.addFilterBefore(jwtTokenFilter(), UsernamePasswordAuthenticationFilter.class); //Adiciona antes no filtro para autenticar
	}
	
	/*Libera o acesso para TODOS os métodos get, post ... vindo de qualquer aplicação, pois a segurança já está feita
	 * pelo autenticação por TOKEN
	 */
	@Bean
	public FilterRegistrationBean<CorsFilter> corsFilter(){
		
		List<String> all = Arrays.asList("*");
		
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedMethods(all);
		config.setAllowedOriginPatterns(all); // Permite qualquer aplicação envie para API(De todo modo com excessão das rotas de cadastro e autenticação o app terá de ter a chave de assinatura para gerar e valida o token)
		config.setAllowedHeaders(all); // Permite que mande qualquer coisa no HEADER
		config.setAllowCredentials(true); // Permite que mande credenciais para API
		
		//As permissões acima para TODAS as rotas e com isso usamos a expressão /**
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		
		CorsFilter corFilter = new CorsFilter(source);
		
		FilterRegistrationBean<CorsFilter> filter = new FilterRegistrationBean<CorsFilter>(corFilter);
		filter.setOrder(Ordered.HIGHEST_PRECEDENCE);
		
		return filter;
		
	}
	
}
