package com.alefesilva.minhasfinancas.api.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.alefesilva.minhasfinancas.api.dto.UsuarioDTO;
import com.alefesilva.minhasfinancas.exception.ErroAutenticacao;
import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.entity.Usuario;
import com.alefesilva.minhasfinancas.service.LancamentoService;
import com.alefesilva.minhasfinancas.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest( controllers = UsuarioResource.class )
@AutoConfigureMockMvc
public class UsuarioResourceTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc;
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception{
		
		//Cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		//Parâmetros que vamos receber do JSON
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		//Criando o usuário
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		//Retornar o usuário autenticado
		Mockito.when( service.autenticar(email, senha) ).thenReturn(usuario);
		
		//Transformando em JSON
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API.concat("/autenticar") ) // Para essa URL
													.accept( JSON ) // Aceita JSON
													.contentType( JSON ) // Enviando objeto do tipo JSON
													.content(json);
		
		mvc
		.perform(request)
		.andExpect( MockMvcResultMatchers.status().isOk() )
		.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
		.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
		.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );
		
	}
	
	@Test
	public void deveRetornarBadRequestAoObterErroDeAutenticacao() throws Exception{
		
		//Cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		//Parâmetros que vamos receber do JSON
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		//Retornar o usuário autenticado
		Mockito.when( service.autenticar(email, senha) ).thenThrow(ErroAutenticacao.class);
		
		//Transformando em JSON
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API.concat("/autenticar") ) // Para essa URL
													.accept( JSON ) // Aceita JSON
													.contentType( JSON ) // Enviando objeto do tipo JSON
													.content(json);
		
		mvc
		.perform(request)
		.andExpect( MockMvcResultMatchers.status().isBadRequest() );
		
	}
	
	@Test
	public void deveCriarUmNovoUsuario() throws Exception{
		
		//Cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		//Parâmetros que vamos receber do JSON
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		//Criando o usuário
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();
		
		//Retornar o usuário que foi criado
		Mockito.when( service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//Transformando em JSON
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API ) // Para essa URL
													.accept( JSON ) // Aceita JSON
													.contentType( JSON ) // Enviando objeto do tipo JSON
													.content(json);
		
		mvc
		.perform(request)
		.andExpect( MockMvcResultMatchers.status().isCreated() )
		.andExpect( MockMvcResultMatchers.jsonPath("id").value(usuario.getId()) )
		.andExpect( MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()) )
		.andExpect( MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()) );
		
	}
	
	@Test
	public void deveRetornarUmBadRequestAoTentarCriarUmUsuarioInvalido() throws Exception{
		
		//Cenário
		String email = "usuario@email.com";
		String senha = "123";
		
		//Parâmetros que vamos receber do JSON
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		
		//Retorna exceção de regra de negócio ao tentar salvar um usuário
		Mockito.when( service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		//Transformando em JSON
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
													.post( API ) // Para essa URL
													.accept( JSON ) // Aceita JSON
													.contentType( JSON ) // Enviando objeto do tipo JSON
													.content(json);
		
		mvc
		.perform(request)
		.andExpect( MockMvcResultMatchers.status().isBadRequest() );
		
	}
	
}




















