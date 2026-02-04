package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.application.exception.AutenticacaoException;
import br.com.bscash.efinanceira.domain.dto.LoginModel;
import br.com.bscash.efinanceira.domain.dto.LoginRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class AuthenticationService {

    private final BackofficeApiConfigService backofficeApiConfigService;
    private WebClient webClient;

    public AuthenticationService(BackofficeApiConfigService backofficeApiConfigService) {
        this.backofficeApiConfigService = backofficeApiConfigService;
    }
    
    private WebClient getWebClient() {
        if (webClient == null) {
            String backofficeApiUrl = backofficeApiConfigService.getBackofficeApiUrl();
            this.webClient = WebClient.builder()
                    .baseUrl(backofficeApiUrl)
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build();
        }
        return webClient;
    }

    public LoginModel autenticar(LoginRequest loginRequest, String userAgent, String clientIp) {
        try {
            return getWebClient().post()
                    .uri("/auth")
                    .header("User-Agent", userAgent != null ? userAgent : "")
                    .header("X-Real-IP", clientIp != null ? clientIp : "")
                    .bodyValue(loginRequest)
                    .retrieve()
                    .bodyToMono(LoginModel.class)
                    .block();
        } catch (WebClientResponseException.Unauthorized e) {
            throw new AutenticacaoException("Credenciais inválidas", e);
        } catch (WebClientResponseException e) {
            throw new AutenticacaoException("Erro ao autenticar com o backoffice: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new AutenticacaoException("Erro inesperado ao autenticar", e);
        }
    }
}
