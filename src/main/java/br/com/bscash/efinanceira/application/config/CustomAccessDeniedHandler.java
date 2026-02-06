package br.com.bscash.efinanceira.application.config;

import br.com.bscash.efinanceira.domain.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
        log.warn("Acesso negado para requisição: {} - {}", request.getRequestURI(), accessDeniedException.getMessage());
        
        int statusCode = HttpServletResponse.SC_FORBIDDEN;
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.error("Acesso negado. Você não tem permissão para acessar este recurso.");

        OutputStream responseStream = response.getOutputStream();
        objectMapper.writeValue(responseStream, apiResponse);
        responseStream.flush();
    }
}
