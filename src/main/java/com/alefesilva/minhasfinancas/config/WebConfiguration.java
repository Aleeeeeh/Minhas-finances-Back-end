package com.alefesilva.minhasfinancas.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@EnableWebMvc
@Configuration
public class WebConfiguration implements WebMvcConfigurer{
	/*addMapping( Coloca a URL das rotas nesse caso selecionamos para TODAS), e allowedOrigins( URL das aplicações ) que
	 * irão enviar requisições para nossa API.allowedMethods(Métodos que poderão ser utilizados para receber requisições.
	 * @Configuration para que o springBoot reconheça essa classe como sendo de configuração.
	 */
	
	@Override
	public void addCorsMappings( CorsRegistry registry ) {
		registry.addMapping("/**").allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
	}
}
