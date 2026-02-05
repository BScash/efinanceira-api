package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.model.TbApi;
import br.com.bscash.efinanceira.infrastructure.repository.ApiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class BackofficeApiConfigService {
    
    private final ApiRepository apiRepository;
    
    private String backofficeApiUrl;
    
    public BackofficeApiConfigService(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }
    
    @PostConstruct
    public void inicializarUrlBackoffice() {
        log.info("Buscando URL do BackOffice no banco de dados (tabela manager.tb_api, nome='MS_BACKOFFICE')...");
        
        var apiOptional = apiRepository.buscarBackofficeApi();
        
        if (apiOptional.isEmpty()) {
            throw new IllegalStateException(
                "Registro MS_BACKOFFICE não encontrado no banco de dados. " +
                "É necessário criar um registro na tabela manager.tb_api com nome='MS_BACKOFFICE' e baseuri preenchido."
            );
        }
        
        TbApi api = apiOptional.get();
        String urlDoBanco = api.getBaseuri();
        
        if (urlDoBanco == null || urlDoBanco.trim().isEmpty()) {
            throw new IllegalStateException(
                "Registro MS_BACKOFFICE encontrado no banco de dados, mas o campo baseuri está vazio ou nulo. " +
                "É necessário preencher o campo baseuri na tabela manager.tb_api para o registro com nome='MS_BACKOFFICE'."
            );
        }
        
        this.backofficeApiUrl = urlDoBanco.trim();
        log.info("URL do BackOffice carregada com sucesso do banco de dados: {}", this.backofficeApiUrl);
    }
    
    public String getBackofficeApiUrl() {
        if (backofficeApiUrl == null) {
            throw new IllegalStateException("URL do BackOffice não foi inicializada. Verifique se o método @PostConstruct foi executado.");
        }
        return backofficeApiUrl;
    }
}
