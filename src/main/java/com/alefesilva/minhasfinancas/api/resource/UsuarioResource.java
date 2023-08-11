package com.alefesilva.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alefesilva.minhasfinancas.api.dto.TokenDTO;
import com.alefesilva.minhasfinancas.api.dto.UsuarioDTO;
import com.alefesilva.minhasfinancas.exception.ErroAutenticacao;
import com.alefesilva.minhasfinancas.exception.RegraNegocioException;
import com.alefesilva.minhasfinancas.model.entity.Usuario;
import com.alefesilva.minhasfinancas.service.JwtService;
import com.alefesilva.minhasfinancas.service.LancamentoService;
import com.alefesilva.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {
	
	private final UsuarioService service;
	
	private final LancamentoService lancamentoService;
	
	private final JwtService jwtService; //Lembrete: Por ter apenas uma implementação para essa interface, não preciso importar pelo impl
	
	@PostMapping("/autenticar")
	public ResponseEntity<?> autenticar( @RequestBody UsuarioDTO dto ) { //? Pois pode retornar mais de um objeto
		try {
			Usuario usuarioAutenticado = service.autenticar(dto.getEmail(), dto.getSenha());
			String token = jwtService.gerarToken(usuarioAutenticado); // Gera o token
			TokenDTO tokenDTO = new TokenDTO(usuarioAutenticado.getNome(), token); // Instancia a classe TokenDTO, passando o nome do usuário autenticado e o token gerado
			
			return ResponseEntity.ok(tokenDTO);
		}catch(ErroAutenticacao e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

	//RequestBody serve para pegarmos o JSON recebido da requisição e atribuir os atributos a classe usuarioDTO
	//http://localhost:8080/api/usuarios
	@PostMapping
	public ResponseEntity<?> salvar( @RequestBody UsuarioDTO dto ) {
		
		Usuario usuario = Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity<>(usuarioSalvo, HttpStatus.CREATED);
		}catch(RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity<?> obterSaldo( @PathVariable("id") Long id ) {
		Optional<Usuario> usuario = service.obterPorId(id);
		
		if(!usuario.isPresent()) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		return ResponseEntity.ok(saldo);
	}
	
	@GetMapping
	public ResponseEntity<?> listagemUsuariosCadastrados(){
		try {
			List<Usuario> listaUsuarios = service.retornaUsuariosCadastrados();
			return ResponseEntity.ok(listaUsuarios);
		} catch (RegraNegocioException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	
}





