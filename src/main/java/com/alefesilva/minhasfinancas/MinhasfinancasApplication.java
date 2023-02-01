package com.alefesilva.minhasfinancas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@EnableWebMvc
public class MinhasfinancasApplication implements WebMvcConfigurer{
	
	/*addMapping( Coloca a URL das rotas nesse caso selecionamos para TODAS), e allowedOrigins( URL das aplicações ) que
	 * irão enviar requisições para nossa API.allowedMethods(Métodos que poderão ser utilizados para receber requisições.
	 */
	
	@Override
	public void addCorsMappings( CorsRegistry registry ) {
		registry.addMapping("/**").allowedMethods("GET","POST","PUT","DELETE","OPTIONS");
	}

	public static void main(String[] args) {
		SpringApplication.run(MinhasfinancasApplication.class, args);
	}

}
