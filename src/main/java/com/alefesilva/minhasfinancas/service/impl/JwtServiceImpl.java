package com.alefesilva.minhasfinancas.service.impl;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alefesilva.minhasfinancas.model.entity.Usuario;
import com.alefesilva.minhasfinancas.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

//Geramos as classes automáticamente pelo autoComplete, depois criamos a lógica de cada método
@Service
public class JwtServiceImpl implements JwtService{
	
	@Value("${jwt.expiracao}")
	private String expiracao;
	
	@Value("${jwt.chave-assinatura}")
	private String chaveAssinatura;

	@Override
	public String gerarToken(Usuario usuario) {
		long exp = Long.valueOf(expiracao); //Converte pro tipo long
		LocalDateTime dataHoraExpiracao = LocalDateTime.now().plusMinutes(exp); //Adiciona o tempo de exp a hora atual
		Instant instant = dataHoraExpiracao.atZone(ZoneId.systemDefault()).toInstant(); //Pegando do fuso horário do SO
		Date data = Date.from(instant); // Do pacote util, convertendo para o tipo date
		
		String token = Jwts
						.builder()
						.setExpiration(data)
						.setSubject(usuario.getEmail()) //Identificação do usuário, pode ser email, idUser etc ...
						.signWith( SignatureAlgorithm.HS512, chaveAssinatura) //Algoritmo de criptografia e nossa chave única criada no app.properties
					    .compact(); // Constrói o token
		return token;
	}

	@Override
	public Claims obterClaims(String token) throws ExpiredJwtException {
		/* Claims são as informações contidas no token, passamos a chave de assinatura e o token em si
		 * e conseguimos resgatar as informação contidas dentro dele
		 */
		
		return Jwts
				.parser()
				.setSigningKey(chaveAssinatura)
				.parseClaimsJws(token)
				.getBody();
	}

	@Override
	public boolean isTokenValido(String token) {
		try {
			Claims claims = obterClaims(token);
			Date dataEx = claims.getExpiration(); // Date pacote util
			LocalDateTime dataExpiracao = dataEx.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(); // Data convertida para pegando do fuso horário do SO
			boolean dataHoraAtualIsAfterExpiracao = LocalDateTime.now().isAfter(dataExpiracao); //Se a data é maior que a data de expiração do token
			
			return !dataHoraAtualIsAfterExpiracao; // Retorna que não é maior que a data do token, se for dispara excessão
		}catch(ExpiredJwtException e) {
			return false;
		}
	}

	@Override
	public String obterLoginUsuario(String token) {
		Claims claims = obterClaims(token);
		
		return claims.getSubject(); //Simplesmente irá retornar o identificador do usuário assinado no token
	}

}
