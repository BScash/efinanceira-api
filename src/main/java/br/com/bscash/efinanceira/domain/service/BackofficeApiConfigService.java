package br.com.bscash.efinanceira.domain.service;

import br.com.bscash.efinanceira.domain.model.TbApi;
import br.com.bscash.efinanceira.infrastructure.repository.ApiRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Slf4j
@Service
public class BackofficeApiConfigService {
    
    private static final String SEPARADOR = "========================================";
    
    private final ApiRepository apiRepository;
    
    private String backofficeApiUrl;
    
    public BackofficeApiConfigService(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }
    
    @PostConstruct
    public void inicializarUrlBackoffice() {
        log.info(SEPARADOR);
        log.info("INICIANDO DIAGNÓSTICO DO BANCO DE DADOS");
        log.info(SEPARADOR);
        
        diagnosticarBancoDados();
        buscarEValidarBackofficeApi();
        
        log.info(SEPARADOR);
        log.info("SUCESSO: URL do BackOffice carregada!");
        log.info("URL: {}", this.backofficeApiUrl);
        log.info(SEPARADOR);
    }
    
    private void diagnosticarBancoDados() {
        String bancoAtual = apiRepository.verificarBancoAtual();
        log.info("Banco de dados atual: {}", bancoAtual);
        
        log.info("---");
        listarSchemas();
        
        log.info("---");
        verificarSchemaManager();
        
        log.info("---");
        listarTabelasManager();
        
        log.info("---");
        verificarTabelaTbApi();
        
        log.info("---");
        int totalRegistros = apiRepository.contarRegistrosTbApi();
        log.info("Total de registros em 'manager.tb_api': {}", totalRegistros);
        
        log.info("---");
        listarRegistrosTbApi();
    }
    
    private void listarSchemas() {
        log.info("Schemas disponíveis no banco:");
        var schemas = apiRepository.listarSchemas();
        if (schemas.isEmpty()) {
            log.warn("Nenhum schema encontrado ou erro ao listar schemas");
        } else {
            schemas.forEach(schema -> log.info("  - {}", schema));
        }
    }
    
    private void verificarSchemaManager() {
        boolean schemaManagerExiste = apiRepository.verificarSchemaManager();
        log.info("Schema 'manager' existe: {}", schemaManagerExiste);
        
        if (!schemaManagerExiste) {
            log.error("Schema 'manager' NÃO encontrado no banco de dados!");
            log.error("É necessário criar o schema 'manager' antes de continuar.");
            throw new IllegalStateException(
                "Schema 'manager' não encontrado no banco de dados. " +
                "É necessário criar o schema 'manager' antes de usar a aplicação."
            );
        }
    }
    
    private void listarTabelasManager() {
        log.info("Tabelas dentro do schema 'manager':");
        var tabelas = apiRepository.listarTabelasManager();
        if (tabelas.isEmpty()) {
            log.warn("Nenhuma tabela encontrada no schema 'manager'");
        } else {
            tabelas.forEach(tabela -> log.info("  - {}", tabela));
        }
    }
    
    private void verificarTabelaTbApi() {
        boolean tabelaTbApiExiste = apiRepository.verificarTabelaTbApi();
        log.info("Tabela 'manager.tb_api' existe: {}", tabelaTbApiExiste);
        
        if (!tabelaTbApiExiste) {
            log.error("Tabela 'manager.tb_api' NÃO encontrada!");
            log.error("É necessário criar a tabela 'manager.tb_api' antes de continuar.");
            throw new IllegalStateException(
                "Tabela 'manager.tb_api' não encontrada no banco de dados. " +
                "É necessário criar a tabela antes de usar a aplicação."
            );
        }
    }
    
    private void listarRegistrosTbApi() {
        log.info("Registros encontrados em 'manager.tb_api':");
        var todosRegistros = apiRepository.listarTodosRegistrosTbApi();
        if (todosRegistros.isEmpty()) {
            log.warn("Nenhum registro encontrado na tabela 'manager.tb_api'");
        } else {
            log.info("Total de registros listados: {}", todosRegistros.size());
            todosRegistros.forEach(this::logarRegistro);
        }
    }
    
    private void logarRegistro(TbApi api) {
        log.info("  --- Registro ID: {} ---", api.getIdapi());
        log.info("    Nome: {}", api.getNome());
        log.info("    BaseURI: {}", api.getBaseuri() != null ? api.getBaseuri() : "(NULL ou vazio)");
        log.info("    Situação: {}", api.getSituacao() != null ? api.getSituacao() : "(NULL)");
        log.info("    Data Inclusão: {}", api.getDatainclusao());
        log.info("    Data Alteração: {}", api.getDataalteracao());
        log.info("    BaseURI Authentication: {}", api.getBaseuriauthentication());
        log.info("    Client ID: {}", api.getClientid());
        log.info("    Documentação: {}", api.getDocumentacao());
    }
    
    private void buscarEValidarBackofficeApi() {
        log.info(SEPARADOR);
        log.info("Buscando URL do BackOffice no banco de dados (tabela manager.tb_api, nome='MS_BACKOFFICE')...");
        
        var apiOptional = apiRepository.buscarBackofficeApi();
        
        if (apiOptional.isEmpty()) {
            logarErroRegistroNaoEncontrado();
            throw new IllegalStateException(
                "Registro MS_BACKOFFICE não encontrado no banco de dados. " +
                "É necessário criar um registro na tabela manager.tb_api com nome='MS_BACKOFFICE' e baseuri preenchido. " +
                "Verifique os logs acima para ver todos os registros existentes na tabela."
            );
        }
        
        TbApi api = apiOptional.get();
        String urlDoBanco = api.getBaseuri();
        
        if (urlDoBanco == null || urlDoBanco.trim().isEmpty()) {
            logarErroBaseUriVazio(api);
            throw new IllegalStateException(
                "Registro MS_BACKOFFICE encontrado no banco de dados, mas o campo baseuri está vazio ou nulo. " +
                "É necessário preencher o campo baseuri na tabela manager.tb_api para o registro com nome='MS_BACKOFFICE'."
            );
        }
        
        this.backofficeApiUrl = urlDoBanco.trim();
    }
    
    private void logarErroRegistroNaoEncontrado() {
        log.error(SEPARADOR);
        log.error("ERRO: Registro MS_BACKOFFICE não encontrado!");
        log.error(SEPARADOR);
        log.error("Possíveis causas:");
        log.error("  1. Não existe registro com nome='MS_BACKOFFICE'");
        log.error("  2. O registro existe mas situacao não é NULL nem '1'");
        log.error("  3. O registro existe mas baseuri está vazio ou NULL");
        log.error("");
        log.error("Solução: Criar ou atualizar um registro na tabela manager.tb_api:");
        log.error("  INSERT INTO manager.tb_api (nome, baseuri, situacao, datainclusao)");
        log.error("  VALUES ('MS_BACKOFFICE', 'http://dev.bscash.com.br:8180/backoffice', '1', CURRENT_TIMESTAMP);");
        log.error(SEPARADOR);
    }
    
    private void logarErroBaseUriVazio(TbApi api) {
        log.error(SEPARADOR);
        log.error("ERRO: Registro MS_BACKOFFICE encontrado mas baseuri está vazio!");
        log.error(SEPARADOR);
        log.error("Registro encontrado:");
        log.error("  ID: {}", api.getIdapi());
        log.error("  Nome: {}", api.getNome());
        log.error("  BaseURI: (NULL ou vazio)");
        log.error("  Situação: {}", api.getSituacao());
        log.error("");
        log.error("Solução: Atualizar o campo baseuri:");
        log.error("  UPDATE manager.tb_api SET baseuri = 'http://dev.bscash.com.br:8180/backoffice' WHERE nome = 'MS_BACKOFFICE';");
        log.error(SEPARADOR);
    }
    
    public String getBackofficeApiUrl() {
        if (backofficeApiUrl == null) {
            throw new IllegalStateException("URL do BackOffice não foi inicializada. Verifique se o método @PostConstruct foi executado.");
        }
        return backofficeApiUrl;
    }
}
