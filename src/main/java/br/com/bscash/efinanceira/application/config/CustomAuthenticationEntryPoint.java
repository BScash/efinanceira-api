package br.com.bscash.efinanceira.application.config;

import br.com.bscash.efinanceira.domain.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
    
        int statusCode = HttpServletResponse.SC_UNAUTHORIZED;
        response.setStatus(statusCode);
        response.setContentType("application/json;charset=UTF-8");

        ApiResponse<?> apiResponse = ApiResponse.error("Token inválido ou ausente. Autenticação necessária.");

        OutputStream responseStream = response.getOutputStream();
        objectMapper.writeValue(responseStream, apiResponse);
        responseStream.flush();
    }
}
