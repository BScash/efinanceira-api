package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.model.TbApi;
import br.com.bscash.efinanceira.infrastructure.repository.ApiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class BackofficeApiConfigService {
    
    private final ApiRepository apiRepository;
    private final String defaultBackofficeUrl;
    
    private String backofficeApiUrl;
    
    public BackofficeApiConfigService(
            ApiRepository apiRepository,
            @Value("${backoffice.api.url}") String defaultBackofficeUrl) {
        this.apiRepository = apiRepository;
        this.defaultBackofficeUrl = defaultBackofficeUrl;
    }
    
    @PostConstruct
    public void inicializarUrlBackoffice() {
        try {
            log.info("Tentando buscar URL do BackOffice no banco de dados...");
            
            var apiOptional = apiRepository.buscarBackofficeApi();
            
            if (apiOptional.isPresent()) {
                TbApi api = apiOptional.get();
                String urlDoBanco = api.getBaseuri();
                
                if (urlDoBanco != null && !urlDoBanco.trim().isEmpty()) {
                    this.backofficeApiUrl = urlDoBanco.trim();
                    log.info("URL do BackOffice carregada do banco de dados: {}", this.backofficeApiUrl);
                } else {
                    this.backofficeApiUrl = defaultBackofficeUrl;
                    log.warn("Registro MS_BACKOFFICE encontrado no banco, mas baseuri está vazio. Usando URL padrão: {}", defaultBackofficeUrl);
                }
            } else {
                this.backofficeApiUrl = defaultBackofficeUrl;
                log.warn("Registro MS_BACKOFFICE não encontrado no banco de dados. Usando URL padrão: {}", defaultBackofficeUrl);
            }
        } catch (Exception e) {
            this.backofficeApiUrl = defaultBackofficeUrl;
            log.error("Erro ao buscar URL do BackOffice no banco de dados. Usando URL padrão: {}", defaultBackofficeUrl, e);
        }
    }
    
    public String getBackofficeApiUrl() {
        return backofficeApiUrl;
    }
}
