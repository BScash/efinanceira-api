package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.application.exception.TokenInvalidoException;
import br.com.bscash.efinanceira.domain.model.UsuarioAutenticado;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class TokenService {

    @Value("${jwt.token.secret}")
    private String jwtSecret;
    
    @Value("${jwt.token.expiration-time-in-minutes:1000}")
    private Long expirationTimeInMinutes;
    
    @PostConstruct
    public void init() {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            log.error("JWT secret não configurado! Verifique a propriedade jwt.token.secret no application.yml");
        } else {
            log.debug("JWT secret configurado (tamanho: {} caracteres)", jwtSecret.length());
        }
    }

    public boolean isAccessTokenValido(String token) {
        try {
            if (jwtSecret == null || jwtSecret.isEmpty()) {
                log.error("JWT secret não configurado!");
                return false;
            }
            
            SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            log.debug("Token JWT válido");
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("Token JWT expirado: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("Erro de assinatura JWT - O secret pode estar incorreto ou o algoritmo não corresponde. Erro: {}", e.getMessage());
            return false;
        } catch (JwtException e) {
            log.error("Token JWT inválido: {} - {}", e.getClass().getSimpleName(), e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Erro inesperado ao validar token JWT: {} - {}", e.getClass().getSimpleName(), e.getMessage(), e);
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
    
    public String gerarToken(Long idUsuario) {
        if (jwtSecret == null || jwtSecret.isEmpty()) {
            log.error("JWT secret não configurado!");
            throw new IllegalStateException("JWT secret não configurado");
        }
        
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        
        Date agora = new Date();
        Date expiracao = new Date(agora.getTime() + (expirationTimeInMinutes * 60 * 1000));
        
        return Jwts.builder()
                .subject(idUsuario.toString())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(key)
                .compact();
    }
    
    public String gerarRefreshToken() {
        return UUID.randomUUID().toString();
    }
}
