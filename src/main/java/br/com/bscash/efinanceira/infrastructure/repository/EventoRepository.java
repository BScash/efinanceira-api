package br.com.bscash.efinanceira.infrastructure.repository;

import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepository {
    
    private static final String PARAM_STATUS_EVENTO = "statusEvento";
    private static final String PARAM_NUMERO_RECIBO = "numeroRecibo";
    private static final String PARAM_CPF = "cpf";
    private static final String PARAM_NOME = "nome";
    private static final String PARAM_ID_LOTE = "idLote";
    private static final String PARAM_ID_PESSOA = "idPessoa";
    private static final String PARAM_ID_CONTA = "idConta";
    private static final String PARAM_DATA_INICIO = "dataInicio";
    private static final String PARAM_DATA_FIM = "dataFim";
    private static final String PARAM_LIMITE = "limite";
    private static final String PARAM_OFFSET = "offset";
    private static final String PARAM_OCORRENCIAS_JSON = "ocorrenciasJson";
    private static final String PARAM_ID_EVENTO = "idEvento";
    private static final String PARAM_ID_USUARIO_ALTERACAO = "idUsuarioAlteracao";
    private static final String PARAM_DATA_ALTERACAO = "dataAlteracao";
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    public static class RegistrarEventoParams {
        private final Long idLote;
        private final Long idPessoa;
        private final Long idConta;
        private final String cpf;
        private final String nome;
        private final Long numeroConta;
        private final String digitoConta;
        private final java.math.BigDecimal saldoAtual;
        private final java.math.BigDecimal totCreditos;
        private final java.math.BigDecimal totDebitos;
        private final String idEventoXml;
        private final String statusEvento;
        private final String ocorrenciasJson;
        private final String numeroRecibo;
        private final Integer indRetificacao;
        private final Long idUsuarioInclusao;
        
        private RegistrarEventoParams(Builder builder) {
            this.idLote = builder.idLote;
            this.idPessoa = builder.idPessoa;
            this.idConta = builder.idConta;
            this.cpf = builder.cpf;
            this.nome = builder.nome;
            this.numeroConta = builder.numeroConta;
            this.digitoConta = builder.digitoConta;
            this.saldoAtual = builder.saldoAtual;
            this.totCreditos = builder.totCreditos;
            this.totDebitos = builder.totDebitos;
            this.idEventoXml = builder.idEventoXml;
            this.statusEvento = builder.statusEvento;
            this.ocorrenciasJson = builder.ocorrenciasJson;
            this.numeroRecibo = builder.numeroRecibo;
            this.indRetificacao = builder.indRetificacao;
            this.idUsuarioInclusao = builder.idUsuarioInclusao;
        }
        
        public static Builder builder() {
            return new Builder();
        }
        
        public Long getIdLote() { return idLote; }
        public Long getIdPessoa() { return idPessoa; }
        public Long getIdConta() { return idConta; }
        public String getCpf() { return cpf; }
        public String getNome() { return nome; }
        public Long getNumeroConta() { return numeroConta; }
        public String getDigitoConta() { return digitoConta; }
        public java.math.BigDecimal getSaldoAtual() { return saldoAtual; }
        public java.math.BigDecimal getTotCreditos() { return totCreditos; }
        public java.math.BigDecimal getTotDebitos() { return totDebitos; }
        public String getIdEventoXml() { return idEventoXml; }
        public String getStatusEvento() { return statusEvento; }
        public String getOcorrenciasJson() { return ocorrenciasJson; }
        public String getNumeroRecibo() { return numeroRecibo; }
        public Integer getIndRetificacao() { return indRetificacao; }
        public Long getIdUsuarioInclusao() { return idUsuarioInclusao; }
        
        public static class Builder {
            private Long idLote;
            private Long idPessoa;
            private Long idConta;
            private String cpf;
            private String nome;
            private Long numeroConta;
            private String digitoConta;
            private java.math.BigDecimal saldoAtual;
            private java.math.BigDecimal totCreditos;
            private java.math.BigDecimal totDebitos;
            private String idEventoXml;
            private String statusEvento;
            private String ocorrenciasJson;
            private String numeroRecibo;
            private Integer indRetificacao;
            private Long idUsuarioInclusao;
            
            public Builder idLote(Long idLote) { this.idLote = idLote; return this; }
            public Builder idPessoa(Long idPessoa) { this.idPessoa = idPessoa; return this; }
            public Builder idConta(Long idConta) { this.idConta = idConta; return this; }
            public Builder cpf(String cpf) { this.cpf = cpf; return this; }
            public Builder nome(String nome) { this.nome = nome; return this; }
            public Builder numeroConta(Long numeroConta) { this.numeroConta = numeroConta; return this; }
            public Builder digitoConta(String digitoConta) { this.digitoConta = digitoConta; return this; }
            public Builder saldoAtual(java.math.BigDecimal saldoAtual) { this.saldoAtual = saldoAtual; return this; }
            public Builder totCreditos(java.math.BigDecimal totCreditos) { this.totCreditos = totCreditos; return this; }
            public Builder totDebitos(java.math.BigDecimal totDebitos) { this.totDebitos = totDebitos; return this; }
            public Builder idEventoXml(String idEventoXml) { this.idEventoXml = idEventoXml; return this; }
            public Builder statusEvento(String statusEvento) { this.statusEvento = statusEvento; return this; }
            public Builder ocorrenciasJson(String ocorrenciasJson) { this.ocorrenciasJson = ocorrenciasJson; return this; }
            public Builder numeroRecibo(String numeroRecibo) { this.numeroRecibo = numeroRecibo; return this; }
            public Builder indRetificacao(Integer indRetificacao) { this.indRetificacao = indRetificacao; return this; }
            public Builder idUsuarioInclusao(Long idUsuarioInclusao) { this.idUsuarioInclusao = idUsuarioInclusao; return this; }
            
            public RegistrarEventoParams build() {
                return new RegistrarEventoParams(this);
            }
        }
    }
    
    public static class BuscarEventosParams {
        private final Long idLote;
        private final Long idPessoa;
        private final Long idConta;
        private final String cpf;
        private final String nome;
        private final String statusEvento;
        private final String numeroRecibo;
        private final Boolean ehRetificacao;
        private final LocalDateTime dataInicio;
        private final LocalDateTime dataFim;
        private final Integer limite;
        private final Integer offset;
        
        public BuscarEventosParams(Long idLote, Long idPessoa, Long idConta, String cpf, String nome,
                                   String statusEvento, String numeroRecibo, Boolean ehRetificacao,
                                   LocalDateTime dataInicio, LocalDateTime dataFim, Integer limite, Integer offset) {
            this.idLote = idLote;
            this.idPessoa = idPessoa;
            this.idConta = idConta;
            this.cpf = cpf;
            this.nome = nome;
            this.statusEvento = statusEvento;
            this.numeroRecibo = numeroRecibo;
            this.ehRetificacao = ehRetificacao;
            this.dataInicio = dataInicio;
            this.dataFim = dataFim;
            this.limite = limite;
            this.offset = offset;
        }
        
        public Long getIdLote() { return idLote; }
        public Long getIdPessoa() { return idPessoa; }
        public Long getIdConta() { return idConta; }
        public String getCpf() { return cpf; }
        public String getNome() { return nome; }
        public String getStatusEvento() { return statusEvento; }
        public String getNumeroRecibo() { return numeroRecibo; }
        public Boolean getEhRetificacao() { return ehRetificacao; }
        public LocalDateTime getDataInicio() { return dataInicio; }
        public LocalDateTime getDataFim() { return dataFim; }
        public Integer getLimite() { return limite; }
        public Integer getOffset() { return offset; }
    }
    
    private static final String BUSCAR_EVENTOS_SQL = """
        SELECT 
            e.idevento,
            e.idlote,
            e.idpessoa,
            e.idconta,
            e.cpf,
            e.nome,
            e.numeroconta,
            e.digitoconta,
            e.saldoatual,
            e.totcreditos,
            e.totdebitos,
            e.ideventoxml,
            e.statusevento,
            e.ocorrenciasefinanceirajson,
            e.datacriacao,
            e.numerorecibo,
            e.indretificacao,
            e.situacao,
            e.idusuarioinclusao,
            e.idusuarioalteracao,
            e.idusuarioalteracaosituacao,
            e.datainclusao,
            e.dataalteracao,
            e.dataalteracaosituacao
        FROM efinanceira.tb_efinanceira_evento e
        """;
    
    public EventoRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Long registrarEvento(Long idLote, Long idPessoa, Long idConta, String cpf, String nome,
                                Long numeroConta, String digitoConta, BigDecimal saldoAtual,
                                BigDecimal totCreditos, BigDecimal totDebitos, String idEventoXml,
                                String statusEvento, String ocorrenciasJson, String numeroRecibo,
                                Integer indRetificacao, Long idUsuarioInclusao) {
        RegistrarEventoParams params = RegistrarEventoParams.builder()
            .idLote(idLote)
            .idPessoa(idPessoa)
            .idConta(idConta)
            .cpf(cpf)
            .nome(nome)
            .numeroConta(numeroConta)
            .digitoConta(digitoConta)
            .saldoAtual(saldoAtual)
            .totCreditos(totCreditos)
            .totDebitos(totDebitos)
            .idEventoXml(idEventoXml)
            .statusEvento(statusEvento)
            .ocorrenciasJson(ocorrenciasJson)
            .numeroRecibo(numeroRecibo)
            .indRetificacao(indRetificacao)
            .idUsuarioInclusao(idUsuarioInclusao)
            .build();
        return registrarEvento(params);
    }
    
    private Long registrarEvento(RegistrarEventoParams p) {
        LocalDateTime agora = LocalDateTime.now();
        String cpfNormalizado = normalizarCpf(p.getCpf());
        String statusFinal = (p.getStatusEvento() != null && !p.getStatusEvento().isBlank()) 
                            ? p.getStatusEvento().toUpperCase() 
                            : "GERADO";
        Integer indRetificacaoFinal = (p.getIndRetificacao() != null) ? p.getIndRetificacao() : 0;
        String numeroReciboNormalizado = normalizarNumeroRecibo(p.getNumeroRecibo());
        
        String sql = """
            INSERT INTO efinanceira.tb_efinanceira_evento 
                (idlote, idpessoa, idconta, cpf, nome, numeroconta, digitoconta,
                 saldoatual, totcreditos, totdebitos, ideventoxml, statusevento,
                 ocorrenciasefinanceirajson, numerorecibo, indretificacao,
                 datacriacao, situacao, idusuarioinclusao, datainclusao)
            VALUES 
                (:idLote, :idPessoa, :idConta, :cpf, :nome, :numeroConta, :digitoConta,
                 :saldoAtual, :totCreditos, :totDebitos, :idEventoXml, :statusEvento,
                 :ocorrenciasJson, :numeroRecibo, :indRetificacao,
                 :dataCriacao, '1', :idUsuarioInclusao, :dataInclusao)
            RETURNING idevento
            """;
        
        MapSqlParameterSource sqlParams = new MapSqlParameterSource()
                .addValue(PARAM_ID_LOTE, p.getIdLote())
                .addValue(PARAM_ID_PESSOA, p.getIdPessoa())
                .addValue(PARAM_ID_CONTA, p.getIdConta())
                .addValue(PARAM_CPF, cpfNormalizado)
                .addValue(PARAM_NOME, p.getNome())
                .addValue("numeroConta", p.getNumeroConta())
                .addValue("digitoConta", p.getDigitoConta())
                .addValue("saldoAtual", p.getSaldoAtual())
                .addValue("totCreditos", p.getTotCreditos())
                .addValue("totDebitos", p.getTotDebitos())
                .addValue("idEventoXml", p.getIdEventoXml())
                .addValue(PARAM_STATUS_EVENTO, statusFinal)
                .addValue(PARAM_OCORRENCIAS_JSON, p.getOcorrenciasJson())
                .addValue(PARAM_NUMERO_RECIBO, numeroReciboNormalizado)
                .addValue("indRetificacao", indRetificacaoFinal)
                .addValue("dataCriacao", agora)
                .addValue("idUsuarioInclusao", p.getIdUsuarioInclusao())
                .addValue("dataInclusao", agora);
        
        return jdbcTemplate.queryForObject(sql, sqlParams, Long.class);
    }
    
    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        
        String cpfLimpo = cpf.replaceAll("\\D", "");
        
        if (cpfLimpo.length() > 11) {
            cpfLimpo = cpfLimpo.substring(0, 11);
        }
        
        return cpfLimpo.isBlank() ? null : cpfLimpo;
    }
    
    private String normalizarNumeroRecibo(String numeroRecibo) {
        if (numeroRecibo == null || numeroRecibo.isBlank()) {
            return null;
        }
        return numeroRecibo.trim();
    }
    
    public List<EventoBancoInfo> buscarEventos(Long idLote, Long idPessoa, Long idConta, 
                                                String cpf, String nome, String statusEvento,
                                                String numeroRecibo, Boolean ehRetificacao,
                                                LocalDateTime dataInicio, LocalDateTime dataFim,
                                                Integer limite, Integer offset) {
        BuscarEventosParams params = new BuscarEventosParams(
            idLote, idPessoa, idConta, cpf, nome, statusEvento, numeroRecibo,
            ehRetificacao, dataInicio, dataFim, limite, offset
        );
        return buscarEventos(params);
    }
    
    private List<EventoBancoInfo> buscarEventos(BuscarEventosParams p) {
        StringBuilder sql = new StringBuilder(BUSCAR_EVENTOS_SQL);
        List<String> whereConditions = new ArrayList<>();
        MapSqlParameterSource sqlParams = new MapSqlParameterSource();
        
        adicionarCondicaoIdLote(whereConditions, sqlParams, p.getIdLote());
        adicionarCondicaoIdPessoa(whereConditions, sqlParams, p.getIdPessoa());
        adicionarCondicaoIdConta(whereConditions, sqlParams, p.getIdConta());
        adicionarCondicaoCpf(whereConditions, sqlParams, p.getCpf());
        adicionarCondicaoNome(whereConditions, sqlParams, p.getNome());
        adicionarCondicaoStatusEvento(whereConditions, sqlParams, p.getStatusEvento());
        adicionarCondicaoNumeroRecibo(whereConditions, sqlParams, p.getNumeroRecibo());
        adicionarCondicaoRetificacao(whereConditions, p.getEhRetificacao());
        adicionarCondicaoDataInicio(whereConditions, sqlParams, p.getDataInicio());
        adicionarCondicaoDataFim(whereConditions, sqlParams, p.getDataFim());
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }
        
        sql.append(" ORDER BY e.datacriacao DESC, e.idevento DESC");
        
        adicionarPaginacao(sql, sqlParams, p.getLimite(), p.getOffset());
        
        return jdbcTemplate.query(sql.toString(), sqlParams, eventoRowMapper());
    }
    
    private void adicionarCondicaoIdLote(List<String> conditions, MapSqlParameterSource params, Long idLote) {
        if (idLote != null) {
            conditions.add("e.idlote = :" + PARAM_ID_LOTE);
            params.addValue(PARAM_ID_LOTE, idLote);
        }
    }
    
    private void adicionarCondicaoIdPessoa(List<String> conditions, MapSqlParameterSource params, Long idPessoa) {
        if (idPessoa != null) {
            conditions.add("e.idpessoa = :" + PARAM_ID_PESSOA);
            params.addValue(PARAM_ID_PESSOA, idPessoa);
        }
    }
    
    private void adicionarCondicaoIdConta(List<String> conditions, MapSqlParameterSource params, Long idConta) {
        if (idConta != null) {
            conditions.add("e.idconta = :" + PARAM_ID_CONTA);
            params.addValue(PARAM_ID_CONTA, idConta);
        }
    }
    
    private void adicionarCondicaoCpf(List<String> conditions, MapSqlParameterSource params, String cpf) {
        if (cpf != null && !cpf.trim().isEmpty()) {
            conditions.add("e.cpf = :" + PARAM_CPF);
            params.addValue(PARAM_CPF, cpf.trim());
        }
    }
    
    private void adicionarCondicaoNome(List<String> conditions, MapSqlParameterSource params, String nome) {
        if (nome != null && !nome.trim().isEmpty()) {
            conditions.add("UPPER(e.nome) LIKE UPPER(:" + PARAM_NOME + ")");
            params.addValue(PARAM_NOME, "%" + nome.trim() + "%");
        }
    }
    
    private void adicionarCondicaoStatusEvento(List<String> conditions, MapSqlParameterSource params, String statusEvento) {
        if (statusEvento != null && !statusEvento.trim().isEmpty()) {
            conditions.add("e.statusevento = :" + PARAM_STATUS_EVENTO);
            params.addValue(PARAM_STATUS_EVENTO, statusEvento.trim().toUpperCase());
        }
    }
    
    private void adicionarCondicaoNumeroRecibo(List<String> conditions, MapSqlParameterSource params, String numeroRecibo) {
        if (numeroRecibo != null && !numeroRecibo.trim().isEmpty()) {
            conditions.add("e.numerorecibo = :" + PARAM_NUMERO_RECIBO);
            params.addValue(PARAM_NUMERO_RECIBO, numeroRecibo.trim());
        }
    }
    
    private void adicionarCondicaoRetificacao(List<String> conditions, Boolean ehRetificacao) {
        if (ehRetificacao != null) {
            if (ehRetificacao) {
                conditions.add("e.indretificacao > 0");
            } else {
                conditions.add("(e.indretificacao IS NULL OR e.indretificacao = 0)");
            }
        }
    }
    
    private void adicionarCondicaoDataInicio(List<String> conditions, MapSqlParameterSource params, LocalDateTime dataInicio) {
        if (dataInicio != null) {
            conditions.add("e.datacriacao >= :" + PARAM_DATA_INICIO);
            params.addValue(PARAM_DATA_INICIO, dataInicio);
        }
    }
    
    private void adicionarCondicaoDataFim(List<String> conditions, MapSqlParameterSource params, LocalDateTime dataFim) {
        if (dataFim != null) {
            conditions.add("e.datacriacao <= :" + PARAM_DATA_FIM);
            params.addValue(PARAM_DATA_FIM, dataFim);
        }
    }
    
    private void adicionarPaginacao(StringBuilder sql, MapSqlParameterSource params, Integer limite, Integer offset) {
        if (limite != null && limite > 0) {
            sql.append(" LIMIT :").append(PARAM_LIMITE);
            params.addValue(PARAM_LIMITE, limite);
            
            if (offset != null && offset > 0) {
                sql.append(" OFFSET :").append(PARAM_OFFSET);
                params.addValue(PARAM_OFFSET, offset);
            }
        }
    }
    
    @Transactional
    public void atualizarEvento(Long idEvento, String statusEvento, String ocorrenciasJson, 
                                String numeroRecibo, Long idUsuarioAlteracao) {
        StringBuilder sql = new StringBuilder("UPDATE efinanceira.tb_efinanceira_evento SET ");
        List<String> updates = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        if (statusEvento != null) {
            updates.add("statusevento = :" + PARAM_STATUS_EVENTO);
            params.addValue(PARAM_STATUS_EVENTO, statusEvento.toUpperCase());
        }
        
        if (ocorrenciasJson != null) {
            updates.add("ocorrenciasefinanceirajson = :" + PARAM_OCORRENCIAS_JSON);
            params.addValue(PARAM_OCORRENCIAS_JSON, ocorrenciasJson);
        }
        
        String numeroReciboNormalizado = normalizarNumeroRecibo(numeroRecibo);
        if (numeroReciboNormalizado != null) {
            updates.add("numerorecibo = :" + PARAM_NUMERO_RECIBO);
            params.addValue(PARAM_NUMERO_RECIBO, numeroReciboNormalizado);
        }
        
        if (updates.isEmpty()) {
            return;
        }
        
        updates.add("dataalteracao = :" + PARAM_DATA_ALTERACAO);
        updates.add("idusuarioalteracao = :" + PARAM_ID_USUARIO_ALTERACAO);
        params.addValue(PARAM_DATA_ALTERACAO, LocalDateTime.now());
        params.addValue(PARAM_ID_USUARIO_ALTERACAO, idUsuarioAlteracao);
        
        sql.append(String.join(", ", updates));
        sql.append(" WHERE idevento = :").append(PARAM_ID_EVENTO);
        params.addValue(PARAM_ID_EVENTO, idEvento);
        
        jdbcTemplate.update(sql.toString(), params);
    }
    
    private RowMapper<EventoBancoInfo> eventoRowMapper() {
        return (rs, rowNum) -> EventoBancoInfo.builder()
                .idEvento(getLong(rs, "idevento"))
                .idLote(getLong(rs, "idlote"))
                .idPessoa(getLong(rs, "idpessoa"))
                .idConta(getLong(rs, "idconta"))
                .cpf(getString(rs, "cpf"))
                .nome(getString(rs, "nome"))
                .numeroConta(getLong(rs, "numeroconta"))
                .digitoConta(getString(rs, "digitoconta"))
                .saldoAtual(getBigDecimal(rs, "saldoatual"))
                .totCreditos(getBigDecimal(rs, "totcreditos"))
                .totDebitos(getBigDecimal(rs, "totdebitos"))
                .idEventoXml(getString(rs, "ideventoxml"))
                .statusEvento(getString(rs, "statusevento"))
                .ocorrenciasJson(getString(rs, "ocorrenciasefinanceirajson"))
                .dataCriacao(getLocalDateTime(rs, "datacriacao"))
                .numeroRecibo(getString(rs, "numerorecibo"))
                .indRetificacao(getInteger(rs, "indretificacao", 0))
                .ehRetificacao(getInteger(rs, "indretificacao", 0) > 0)
                .situacao(getString(rs, "situacao"))
                .idUsuarioInclusao(getLong(rs, "idusuarioinclusao"))
                .idUsuarioAlteracao(getLong(rs, "idusuarioalteracao"))
                .idUsuarioAlteracaoSituacao(getLong(rs, "idusuarioalteracaosituacao"))
                .dataInclusao(getLocalDateTime(rs, "datainclusao"))
                .dataAlteracao(getLocalDateTime(rs, "dataalteracao"))
                .dataAlteracaoSituacao(getLocalDateTime(rs, "dataalteracaosituacao"))
                .build();
    }
    
    private String getString(java.sql.ResultSet rs, String columnName) {
        try {
            return rs.getString(columnName);
        } catch (Exception e) {
            return null;
        }
    }
    
    private Long getLong(java.sql.ResultSet rs, String columnName) {
        try {
            long value = rs.getLong(columnName);
            return rs.wasNull() ? null : value;
        } catch (Exception e) {
            return null;
        }
    }
    
    private Integer getInteger(java.sql.ResultSet rs, String columnName, Integer defaultValue) {
        try {
            int value = rs.getInt(columnName);
            return rs.wasNull() ? defaultValue : value;
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    private BigDecimal getBigDecimal(java.sql.ResultSet rs, String columnName) {
        try {
            BigDecimal value = rs.getBigDecimal(columnName);
            return rs.wasNull() ? null : value;
        } catch (Exception e) {
            return null;
        }
    }
    
    private LocalDateTime getLocalDateTime(java.sql.ResultSet rs, String columnName) {
        try {
            java.sql.Timestamp timestamp = rs.getTimestamp(columnName);
            if (rs.wasNull()) {
                return null;
            }
            return timestamp != null ? timestamp.toLocalDateTime() : null;
        } catch (Exception e) {
            return null;
        }
    }
}
