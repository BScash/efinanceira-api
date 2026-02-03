package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.application.exception.TokenInvalidoException;
import br.com.bscash.efinanceira.domain.model.UsuarioAutenticado;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Service
public class TokenService {

    @Value("${jwt.token.secret}")
    private String jwtSecret;

    public boolean isAccessTokenValido(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public UsuarioAutenticado getUsuarioAutenticado(String token) throws TokenInvalidoException {
        try {
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            Long idUsuarioAutenticado = Long.parseLong(claims.getSubject());
            return new UsuarioAutenticado(idUsuarioAutenticado);
        } catch (ExpiredJwtException e) {
            throw new TokenInvalidoException("Token expirado", e);
        } catch (JwtException e) {
            throw new TokenInvalidoException("Token inválido", e);
        } catch (NumberFormatException e) {
            throw new TokenInvalidoException("Formato do token inválido: ID do usuário não encontrado", e);
        }
    }
}
