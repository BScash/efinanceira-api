package br.com.bscash.efinanceira.infrastructure.repository;

import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventoRepository {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
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
        LocalDateTime agora = LocalDateTime.now();
        String cpfNormalizado = normalizarCpf(cpf);
        String statusFinal = (statusEvento != null && !statusEvento.isBlank()) 
                            ? statusEvento.toUpperCase() 
                            : "GERADO";
        Integer indRetificacaoFinal = (indRetificacao != null) ? indRetificacao : 0;
        
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
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idLote", idLote)
                .addValue("idPessoa", idPessoa)
                .addValue("idConta", idConta)
                .addValue("cpf", cpfNormalizado)
                .addValue("nome", nome)
                .addValue("numeroConta", numeroConta)
                .addValue("digitoConta", digitoConta)
                .addValue("saldoAtual", saldoAtual)
                .addValue("totCreditos", totCreditos)
                .addValue("totDebitos", totDebitos)
                .addValue("idEventoXml", idEventoXml)
                .addValue("statusEvento", statusFinal)
                .addValue("ocorrenciasJson", ocorrenciasJson)
                .addValue("numeroRecibo", numeroRecibo)
                .addValue("indRetificacao", indRetificacaoFinal)
                .addValue("dataCriacao", agora)
                .addValue("idUsuarioInclusao", idUsuarioInclusao)
                .addValue("dataInclusao", agora);
        
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }
    
    private String normalizarCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        
        String cpfLimpo = cpf.replaceAll("[^0-9]", "");
        
        if (cpfLimpo.length() > 11) {
            cpfLimpo = cpfLimpo.substring(0, 11);
        }
        
        return cpfLimpo.isBlank() ? null : cpfLimpo;
    }
    
    public List<EventoBancoInfo> buscarEventos(Long idLote, Long idPessoa, Long idConta, 
                                                String cpf, String nome, String statusEvento,
                                                String numeroRecibo, Boolean ehRetificacao,
                                                LocalDateTime dataInicio, LocalDateTime dataFim,
                                                Integer limite, Integer offset) {
        StringBuilder sql = new StringBuilder(BUSCAR_EVENTOS_SQL);
        List<String> whereConditions = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        if (idLote != null) {
            whereConditions.add("e.idlote = :idLote");
            params.addValue("idLote", idLote);
        }
        
        if (idPessoa != null) {
            whereConditions.add("e.idpessoa = :idPessoa");
            params.addValue("idPessoa", idPessoa);
        }
        
        if (idConta != null) {
            whereConditions.add("e.idconta = :idConta");
            params.addValue("idConta", idConta);
        }
        
        if (cpf != null && !cpf.trim().isEmpty()) {
            whereConditions.add("e.cpf = :cpf");
            params.addValue("cpf", cpf.trim());
        }
        
        if (nome != null && !nome.trim().isEmpty()) {
            whereConditions.add("UPPER(e.nome) LIKE UPPER(:nome)");
            params.addValue("nome", "%" + nome.trim() + "%");
        }
        
        if (statusEvento != null && !statusEvento.trim().isEmpty()) {
            whereConditions.add("e.statusevento = :statusEvento");
            params.addValue("statusEvento", statusEvento.trim().toUpperCase());
        }
        
        if (numeroRecibo != null && !numeroRecibo.trim().isEmpty()) {
            whereConditions.add("e.numerorecibo = :numeroRecibo");
            params.addValue("numeroRecibo", numeroRecibo.trim());
        }
        
        if (ehRetificacao != null) {
            if (ehRetificacao) {
                whereConditions.add("e.indretificacao > 0");
            } else {
                whereConditions.add("(e.indretificacao IS NULL OR e.indretificacao = 0)");
            }
        }
        
        if (dataInicio != null) {
            whereConditions.add("e.datacriacao >= :dataInicio");
            params.addValue("dataInicio", dataInicio);
        }
        
        if (dataFim != null) {
            whereConditions.add("e.datacriacao <= :dataFim");
            params.addValue("dataFim", dataFim);
        }
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }
        
        sql.append(" ORDER BY e.datacriacao DESC, e.idevento DESC");
        
        if (limite != null && limite > 0) {
            sql.append(" LIMIT :limite");
            params.addValue("limite", limite);
            
            if (offset != null && offset > 0) {
                sql.append(" OFFSET :offset");
                params.addValue("offset", offset);
            }
        }
        
        return jdbcTemplate.query(sql.toString(), params, eventoRowMapper());
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
