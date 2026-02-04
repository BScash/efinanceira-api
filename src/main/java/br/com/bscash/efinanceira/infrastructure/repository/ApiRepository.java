package br.com.bscash.efinanceira.infrastructure.repository;

import br.com.bscash.efinanceira.domain.model.TbApi;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class ApiRepository {
    
    private static final String PARAM_NOME = "nome";
    private static final String NOME_BACKOFFICE = "MS_BACKOFFICE";
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    private static final String BUSCAR_POR_NOME_SQL = """
        SELECT 
            idapi,
            apikey,
            dataalteracao,
            dataalteracaosituacao,
            datainclusao,
            situacao,
            baseuri,
            baseuriauthentication,
            clientid,
            dataexpiracao,
            documentacao,
            nome,
            refreshtoken,
            secretkey,
            tokenapi,
            tokencallback,
            tokentype,
            urlcallback,
            userid,
            idusuarioalteracaosituacao,
            idusuarioalteracao,
            idusuarioinclusao,
            chaveprivada
        FROM manager.tb_api
        WHERE nome = :nome
        AND (situacao IS NULL OR situacao = '1')
        LIMIT 1
        """;
    
    public ApiRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Optional<TbApi> buscarBackofficeApi() {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue(PARAM_NOME, NOME_BACKOFFICE);
        
        try {
            List<TbApi> resultados = jdbcTemplate.query(BUSCAR_POR_NOME_SQL, params, new TbApiRowMapper());
            return resultados.isEmpty() ? Optional.empty() : Optional.of(resultados.get(0));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private static class TbApiRowMapper implements RowMapper<TbApi> {
        @Override
        public TbApi mapRow(ResultSet rs, int rowNum) throws SQLException {
            return TbApi.builder()
                    .idapi(rs.getLong("idapi"))
                    .apikey(getString(rs, "apikey"))
                    .dataalteracao(getLocalDateTime(rs, "dataalteracao"))
                    .dataalteracaosituacao(getLocalDateTime(rs, "dataalteracaosituacao"))
                    .datainclusao(getLocalDateTime(rs, "datainclusao"))
                    .situacao(getString(rs, "situacao"))
                    .baseuri(getString(rs, "baseuri"))
                    .baseuriauthentication(getString(rs, "baseuriauthentication"))
                    .clientid(getString(rs, "clientid"))
                    .dataexpiracao(getLocalDateTime(rs, "dataexpiracao"))
                    .documentacao(getString(rs, "documentacao"))
                    .nome(getString(rs, "nome"))
                    .refreshtoken(getString(rs, "refreshtoken"))
                    .secretkey(getString(rs, "secretkey"))
                    .tokenapi(getString(rs, "tokenapi"))
                    .tokencallback(getString(rs, "tokencallback"))
                    .tokentype(getString(rs, "tokentype"))
                    .urlcallback(getString(rs, "urlcallback"))
                    .userid(getString(rs, "userid"))
                    .idusuarioalteracaosituacao(getLong(rs, "idusuarioalteracaosituacao"))
                    .idusuarioalteracao(getLong(rs, "idusuarioalteracao"))
                    .idusuarioinclusao(getLong(rs, "idusuarioinclusao"))
                    .chaveprivada(getString(rs, "chaveprivada"))
                    .build();
        }
        
        private String getString(ResultSet rs, String columnName) throws SQLException {
            String value = rs.getString(columnName);
            return rs.wasNull() ? null : value;
        }
        
        private LocalDateTime getLocalDateTime(ResultSet rs, String columnName) throws SQLException {
            Timestamp timestamp = rs.getTimestamp(columnName);
            return timestamp == null ? null : timestamp.toLocalDateTime();
        }
        
        private Long getLong(ResultSet rs, String columnName) throws SQLException {
            long value = rs.getLong(columnName);
            return rs.wasNull() ? null : value;
        }
    }
}
