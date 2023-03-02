package com.alefesilva.minhasfinancas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter{
	
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
		String senhaCriptografada = passwordEncoder().encode("qwe123");
		
		auth.inMemoryAuthentication().withUser("usuarioLog").password(senhaCriptografada).roles("USER");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable().authorizeRequests().anyRequest().authenticated().and().httpBasic();
	}
	
}
