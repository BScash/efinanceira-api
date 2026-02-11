package br.com.bscash.efinanceira.infrastructure.repository;

import br.com.bscash.efinanceira.domain.model.EventoBancoInfo;
import br.com.bscash.efinanceira.domain.model.LoteBancoInfo;
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
public class LoteRepository {
    
    private static final String PARAM_PERIODO = "periodo";
    private static final String PARAM_AMBIENTE = "ambiente";
    private static final String TIPO_LOTE_MOVIMENTACAO = "MOVIMENTACAO";
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    private static final String BUSCAR_LOTES_SQL = """
        SELECT 
            l.idlote,
            l.periodo,
            l.semestre,
            l.numerolote,
            l.quantidadeeventos,
            l.cnpjdeclarante,
            l.protocoloenvio,
            l.status,
            l.ambiente,
            l.codigorespostaenvio,
            l.descricaorespostaenvio,
            l.codigorespostaconsulta,
            l.descricaorespostaconsulta,
            l.datacriacao,
            l.dataenvio,
            l.dataconfirmacao,
            l.id_lote_original,
            l.caminhoarquivolotexml,
            l.situacao,
            l.idusuarioinclusao,
            l.idusuarioalteracao,
            l.idusuarioalteracaosituacao,
            l.datainclusao,
            l.dataalteracao,
            l.dataalteracaosituacao,
            COUNT(DISTINCT e.idevento) as total_eventos_registrados,
            COUNT(DISTINCT CASE WHEN e.cpf IS NOT NULL AND e.cpf != '' THEN e.cpf END) as total_eventos_com_cpf,
            COUNT(DISTINCT CASE WHEN e.statusevento = 'ERRO' THEN e.idevento END) as total_eventos_com_erro,
            COUNT(DISTINCT CASE WHEN e.statusevento = 'SUCESSO' THEN e.idevento END) as total_eventos_sucesso
        FROM efinanceira.tb_efinanceira_lote l
        LEFT JOIN efinanceira.tb_efinanceira_evento e ON e.idlote = l.idlote
        """;
    
    private static final String BUSCAR_LOTE_POR_PROTOCOLO_SQL = """
        SELECT 
            l.idlote,
            l.periodo,
            l.semestre,
            l.numerolote,
            l.quantidadeeventos,
            l.cnpjdeclarante,
            l.protocoloenvio,
            l.status,
            l.ambiente,
            l.codigorespostaenvio,
            l.descricaorespostaenvio,
            l.codigorespostaconsulta,
            l.descricaorespostaconsulta,
            l.datacriacao,
            l.dataenvio,
            l.dataconfirmacao,
            l.id_lote_original,
            l.caminhoarquivolotexml,
            l.situacao,
            l.idusuarioinclusao,
            l.idusuarioalteracao,
            l.idusuarioalteracaosituacao,
            l.datainclusao,
            l.dataalteracao,
            l.dataalteracaosituacao
        FROM efinanceira.tb_efinanceira_lote l
        WHERE l.protocoloenvio = :protocolo
        ORDER BY l.datacriacao DESC
        LIMIT 1
        """;
    
    private static final String BUSCAR_EVENTOS_DO_LOTE_SQL = """
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
        WHERE e.idlote = :idLote
        ORDER BY e.idevento
        """;
    
    private static final String VERIFICAR_ABERTURA_ENVIADA_SQL = """
        SELECT 
            l.idlote,
            l.protocoloenvio,
            l.codigorespostaenvio,
            l.codigorespostaconsulta,
            l.caminhoarquivolotexml
        FROM efinanceira.tb_efinanceira_lote l
        WHERE l.periodo = :periodo
          AND l.ambiente = :ambiente
          AND (
              LOWER(l.caminhoarquivolotexml) LIKE '%abertura%' OR
              LOWER(l.caminhoarquivolotexml) LIKE '%abert%'
          )
        ORDER BY l.datacriacao DESC
        LIMIT 1
        """;
    
    public LoteRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<LoteBancoInfo> buscarLotes(LocalDateTime dataInicio, LocalDateTime dataFim, 
                                           String periodo, String ambiente, Integer limite) {
        StringBuilder sql = new StringBuilder(BUSCAR_LOTES_SQL);
        List<String> whereConditions = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        if (dataInicio != null) {
            whereConditions.add("l.datacriacao >= :dataInicio");
            params.addValue("dataInicio", dataInicio);
        }
        
        if (dataFim != null) {
            whereConditions.add("l.datacriacao <= :dataFim");
            params.addValue("dataFim", dataFim);
        }
        
        if (periodo != null && !periodo.isBlank()) {
            whereConditions.add("l.periodo = :" + PARAM_PERIODO);
            params.addValue(PARAM_PERIODO, periodo);
        }
        
        if (ambiente != null && !ambiente.trim().isEmpty()) {
            String ambienteUpper = ambiente.trim().toUpperCase();
            if ("TEST".equals(ambienteUpper) || "TESTE".equals(ambienteUpper)) {
                whereConditions.add("l.ambiente = 'HOMOLOG'");
            } else {
                whereConditions.add("l.ambiente = :" + PARAM_AMBIENTE);
                params.addValue(PARAM_AMBIENTE, ambienteUpper);
            }
        }
        
        if (!whereConditions.isEmpty()) {
            sql.append(" WHERE ").append(String.join(" AND ", whereConditions));
        }
        
        sql.append(" GROUP BY l.idlote, l.periodo, l.semestre, l.numerolote, l.quantidadeeventos,");
        sql.append(" l.cnpjdeclarante, l.protocoloenvio, l.status, l.ambiente,");
        sql.append(" l.codigorespostaenvio, l.descricaorespostaenvio, l.codigorespostaconsulta,");
        sql.append(" l.descricaorespostaconsulta, l.datacriacao, l.dataenvio, l.dataconfirmacao,");
        sql.append(" l.id_lote_original, l.caminhoarquivolotexml, l.situacao, l.idusuarioinclusao,");
        sql.append(" l.idusuarioalteracao, l.idusuarioalteracaosituacao, l.datainclusao,");
        sql.append(" l.dataalteracao, l.dataalteracaosituacao");
        sql.append(" ORDER BY l.datacriacao DESC");
        
        if (limite != null && limite > 0) {
            sql.append(" LIMIT :limite");
            params.addValue("limite", limite);
        }
        
        return jdbcTemplate.query(sql.toString(), params, loteRowMapper());
    }
    
    public LoteBancoInfo buscarLotePorProtocolo(String protocolo) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("protocolo", protocolo);
        
        List<LoteBancoInfo> lotes = jdbcTemplate.query(BUSCAR_LOTE_POR_PROTOCOLO_SQL, params, loteRowMapper());
        return lotes.isEmpty() ? null : lotes.get(0);
    }
    
    public List<EventoBancoInfo> buscarEventosDoLote(Long idLote) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idLote", idLote);
        
        return jdbcTemplate.query(BUSCAR_EVENTOS_DO_LOTE_SQL, params, eventoRowMapper());
    }
    
    public boolean verificarAberturaEnviadaParaPeriodo(String periodo, String ambiente) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(PARAM_PERIODO, periodo)
                .addValue(PARAM_AMBIENTE, ambiente);
        
        List<LoteBancoInfo> lotes = jdbcTemplate.query(VERIFICAR_ABERTURA_ENVIADA_SQL, params, loteRowMapper());
        
        if (lotes.isEmpty()) {
            return false;
        }
        
        LoteBancoInfo lote = lotes.get(0);
        boolean temProtocolo = lote.getProtocoloEnvio() != null && !lote.getProtocoloEnvio().isBlank();
        boolean respostaOk = (lote.getCodigoRespostaEnvio() != null && 
                              (lote.getCodigoRespostaEnvio() == 1 || lote.getCodigoRespostaEnvio() == 2)) ||
                             (lote.getCodigoRespostaConsulta() != null && 
                              (lote.getCodigoRespostaConsulta() == 1 || lote.getCodigoRespostaConsulta() == 2));
        
        return temProtocolo && respostaOk;
    }
    
    public Long registrarLote(String periodo, Integer quantidadeEventos, String cnpjDeclarante,
                             String caminhoArquivoXml, String ambiente, Integer numeroLote,
                             Long idLoteOriginal, Long idUsuarioInclusao) {
        Integer semestre = calcularSemestre(periodo);
        LocalDateTime agora = LocalDateTime.now();
        
        if (numeroLote == null) {
            numeroLote = obterProximoNumeroLote(periodo, ambiente);
        }
        
        String sql = """
            INSERT INTO efinanceira.tb_efinanceira_lote 
                (periodo, semestre, numerolote, quantidadeeventos, cnpjdeclarante, 
                 status, ambiente, caminhoarquivolotexml, id_lote_original,
                 datacriacao, situacao, idusuarioinclusao, datainclusao)
            VALUES 
                (:periodo, :semestre, :numeroLote, :quantidadeEventos, :cnpjDeclarante,
                 'GERADO', :ambiente, :caminhoArquivoXml, :idLoteOriginal,
                 :dataCriacao, '1', :idUsuarioInclusao, :dataInclusao)
            RETURNING idlote
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(PARAM_PERIODO, periodo)
                .addValue("semestre", semestre)
                .addValue("numeroLote", numeroLote)
                .addValue("quantidadeEventos", quantidadeEventos)
                .addValue("cnpjDeclarante", cnpjDeclarante)
                .addValue(PARAM_AMBIENTE, ambiente)
                .addValue("caminhoArquivoXml", caminhoArquivoXml)
                .addValue("idLoteOriginal", idLoteOriginal)
                .addValue("dataCriacao", agora)
                .addValue("idUsuarioInclusao", idUsuarioInclusao)
                .addValue("dataInclusao", agora);
        
        return jdbcTemplate.queryForObject(sql, params, Long.class);
    }
    
    private Integer obterProximoNumeroLote(String periodo, String ambiente) {
        String sql = """
            SELECT COALESCE(MAX(numerolote), 0) + 1
            FROM efinanceira.tb_efinanceira_lote
            WHERE periodo = :periodo
              AND ambiente = :ambiente
            """;
        
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(PARAM_PERIODO, periodo)
                .addValue(PARAM_AMBIENTE, ambiente);
        
        Integer proximoNumero = jdbcTemplate.queryForObject(sql, params, Integer.class);
        return proximoNumero != null ? proximoNumero : 1;
    }
    
    @Transactional
    public void atualizarLote(Long idLote, String status, String protocoloEnvio,
                             Integer codigoRespostaEnvio, String descricaoRespostaEnvio, String xmlRespostaEnvio,
                             Integer codigoRespostaConsulta, String descricaoRespostaConsulta, String xmlRespostaConsulta,
                             LocalDateTime dataEnvio, LocalDateTime dataConfirmacao, String ultimoErro,
                             String caminhoArquivoAssinado, String caminhoArquivoCriptografado, Long idUsuarioAlteracao) {
        StringBuilder sql = new StringBuilder("UPDATE efinanceira.tb_efinanceira_lote SET ");
        List<String> updates = new ArrayList<>();
        MapSqlParameterSource params = new MapSqlParameterSource();
        
        if (status != null) {
            updates.add("status = :status");
            params.addValue("status", status);
        }
        
        if (protocoloEnvio != null) {
            updates.add("protocoloenvio = :protocoloEnvio");
            params.addValue("protocoloEnvio", protocoloEnvio);
        }
        
        if (codigoRespostaEnvio != null) {
            updates.add("codigorespostaenvio = :codigoRespostaEnvio");
            params.addValue("codigoRespostaEnvio", codigoRespostaEnvio);
        }
        
        if (descricaoRespostaEnvio != null) {
            updates.add("descricaorespostaenvio = :descricaoRespostaEnvio");
            params.addValue("descricaoRespostaEnvio", descricaoRespostaEnvio);
        }
        
        if (xmlRespostaEnvio != null) {
            updates.add("xmlrespostaenvio = :xmlRespostaEnvio");
            params.addValue("xmlRespostaEnvio", xmlRespostaEnvio);
        }
        
        if (codigoRespostaConsulta != null) {
            updates.add("codigorespostaconsulta = :codigoRespostaConsulta");
            params.addValue("codigoRespostaConsulta", codigoRespostaConsulta);
        }
        
        if (descricaoRespostaConsulta != null) {
            updates.add("descricaorespostaconsulta = :descricaoRespostaConsulta");
            params.addValue("descricaoRespostaConsulta", descricaoRespostaConsulta);
        }
        
        if (xmlRespostaConsulta != null) {
            updates.add("xmlrespostaconsulta = :xmlRespostaConsulta");
            params.addValue("xmlRespostaConsulta", xmlRespostaConsulta);
        }
        
        if (dataEnvio != null) {
            updates.add("dataenvio = :dataEnvio");
            params.addValue("dataEnvio", dataEnvio);
        }
        
        if (dataConfirmacao != null) {
            updates.add("dataconfirmacao = :dataConfirmacao");
            params.addValue("dataConfirmacao", dataConfirmacao);
        }
        
        if (ultimoErro != null) {
            updates.add("ultimoerro = :ultimoErro");
            params.addValue("ultimoErro", ultimoErro);
        }
        
        if (caminhoArquivoAssinado != null) {
            updates.add("caminhoarquivoloteassinadoxml = :caminhoArquivoAssinado");
            params.addValue("caminhoArquivoAssinado", caminhoArquivoAssinado);
        }
        
        if (caminhoArquivoCriptografado != null) {
            updates.add("caminhoarquivolotecriptografadoxml = :caminhoArquivoCriptografado");
            params.addValue("caminhoArquivoCriptografado", caminhoArquivoCriptografado);
        }
        
        if (updates.isEmpty()) {
            return;
        }
        
        updates.add("dataalteracao = :dataAlteracao");
        updates.add("idusuarioalteracao = :idUsuarioAlteracao");
        params.addValue("dataAlteracao", LocalDateTime.now());
        params.addValue("idUsuarioAlteracao", idUsuarioAlteracao);
        
        sql.append(String.join(", ", updates));
        sql.append(" WHERE idlote = :idLote");
        params.addValue("idLote", idLote);
        
        jdbcTemplate.update(sql.toString(), params);
    }
    
    private Integer calcularSemestre(String periodo) {
        if (periodo == null || periodo.length() < 6) {
            return 0;
        }
        
        try {
            int mes = Integer.parseInt(periodo.substring(4, 6));
            if (mes == 1 || mes == 6) return 1;
            if (mes == 2 || mes == 12) return 2;
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
    
    private RowMapper<LoteBancoInfo> loteRowMapper() {
        return (rs, rowNum) -> {
            String caminhoArquivo = getString(rs, "caminhoarquivolotexml");
            String tipoLote = determinarTipoLote(caminhoArquivo);
            
            return LoteBancoInfo.builder()
                    .idLote(getLong(rs, "idlote"))
                    .periodo(getString(rs, PARAM_PERIODO))
                    .semestre(getInteger(rs, "semestre"))
                    .numeroLote(getInteger(rs, "numerolote"))
                    .quantidadeEventos(getInteger(rs, "quantidadeeventos"))
                    .cnpjDeclarante(getString(rs, "cnpjdeclarante"))
                    .protocoloEnvio(getString(rs, "protocoloenvio"))
                    .status(getString(rs, "status"))
                    .ambiente(getString(rs, PARAM_AMBIENTE))
                    .codigoRespostaEnvio(getInteger(rs, "codigorespostaenvio"))
                    .descricaoRespostaEnvio(getString(rs, "descricaorespostaenvio"))
                    .codigoRespostaConsulta(getInteger(rs, "codigorespostaconsulta"))
                    .descricaoRespostaConsulta(getString(rs, "descricaorespostaconsulta"))
                    .dataCriacao(getLocalDateTime(rs, "datacriacao"))
                    .dataEnvio(getLocalDateTime(rs, "dataenvio"))
                    .dataConfirmacao(getLocalDateTime(rs, "dataconfirmacao"))
                    .idLoteOriginal(getLong(rs, "id_lote_original"))
                    .caminhoArquivoXml(caminhoArquivo)
                    .tipoLote(tipoLote)
                    .totalEventosRegistrados(getInteger(rs, "total_eventos_registrados", 0))
                    .totalEventosComCpf(getInteger(rs, "total_eventos_com_cpf", 0))
                    .totalEventosComErro(getInteger(rs, "total_eventos_com_erro", 0))
                    .totalEventosSucesso(getInteger(rs, "total_eventos_sucesso", 0))
                    .ehRetificacao(getLong(rs, "id_lote_original") != null)
                    .situacao(getString(rs, "situacao"))
                    .idUsuarioInclusao(getLong(rs, "idusuarioinclusao"))
                    .idUsuarioAlteracao(getLong(rs, "idusuarioalteracao"))
                    .idUsuarioAlteracaoSituacao(getLong(rs, "idusuarioalteracaosituacao"))
                    .dataInclusao(getLocalDateTime(rs, "datainclusao"))
                    .dataAlteracao(getLocalDateTime(rs, "dataalteracao"))
                    .dataAlteracaoSituacao(getLocalDateTime(rs, "dataalteracaosituacao"))
                    .build();
        };
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
    
    private String determinarTipoLote(String caminhoArquivo) {
        if (caminhoArquivo == null || caminhoArquivo.isBlank()) {
            return TIPO_LOTE_MOVIMENTACAO;
        }
        
        String nomeArquivo = caminhoArquivo.toLowerCase();
        if (nomeArquivo.contains("abertura")) {
            return "ABERTURA";
        } else if (nomeArquivo.contains("fechamento")) {
            return "FECHAMENTO";
        } else if (nomeArquivo.contains("cadastro") || nomeArquivo.contains("declarante")) {
            return "CADASTRO_DECLARANTE";
        } else if (nomeArquivo.contains("movimentacao") || nomeArquivo.contains("movimentação")) {
            return TIPO_LOTE_MOVIMENTACAO;
        }
        
        return TIPO_LOTE_MOVIMENTACAO;
    }
    
    private String getString(java.sql.ResultSet rs, String columnName) {
        try {
            String value = rs.getString(columnName);
            if (rs.wasNull()) {
                return "";
            }
            return value != null ? value : "";
        } catch (Exception e) {
            return "";
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
    
    private Integer getInteger(java.sql.ResultSet rs, String columnName) {
        return getInteger(rs, columnName, null);
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
