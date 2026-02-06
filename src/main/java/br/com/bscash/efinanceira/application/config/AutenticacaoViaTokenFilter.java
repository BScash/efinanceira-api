package br.com.bscash.efinanceira.application.config;

import br.com.bscash.efinanceira.domain.model.UsuarioAutenticado;
import br.com.bscash.efinanceira.domain.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Slf4j
public class AutenticacaoViaTokenFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    public AutenticacaoViaTokenFilter(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = getTokenFromHeader(request);
        if (token != null) {
            log.debug("Token encontrado no header para requisição: {}", request.getRequestURI());
            if (tokenService.isAccessTokenValido(token)) {
                log.debug("Token válido, autenticando usuário...");
                autenticarUsuario(token);
            } else {
                log.debug("Token inválido para requisição: {}", request.getRequestURI());
            }
        } else {
            log.debug("Token não encontrado no header para requisição: {}", request.getRequestURI());
        }

        filterChain.doFilter(request, response);
    }

    private void autenticarUsuario(String token) {
        try {
            UsuarioAutenticado usuarioAutenticado = tokenService.getUsuarioAutenticado(token);
            log.debug("Usuário autenticado com sucesso. ID: {}", usuarioAutenticado.getId());
            
            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    usuarioAutenticado,
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("Autenticação configurada no SecurityContext");
        } catch (Exception e) {
            log.error("Erro ao autenticar usuário com token: {}", e.getMessage(), e);
        }
    }

    private String getTokenFromHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty() || !token.startsWith("Bearer ")) {
            return null;
        }
        return token.substring(7);
    }
}
