package br.com.bscash.efinanceira.infrastructure.repository;

import br.com.bscash.efinanceira.domain.model.DadosPessoaConta;
import br.com.bscash.efinanceira.domain.model.TotaisMovimentacao;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class PessoaContaRepository {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    private static final String BUSCAR_PESSOAS_COM_CONTAS_SQL = """
        SELECT 
            p.idpessoa as IdPessoa,
            p.documento as Documento,
            p.nome as Nome,
            pf.cpf as Cpf,
            COALESCE(pf.nacionalidade, 'BR') as Nacionalidade,
            COALESCE(p.telefone, '') as Telefone,
            COALESCE(p.email, '') as Email,
            c.idconta as IdConta,
            COALESCE(CAST(c.numeroconta AS TEXT), '') as NumeroConta,
            COALESCE(c.digitoconta, '') as DigitoConta,
            COALESCE(c.saldoatual, 0) as SaldoAtual,
            COALESCE(e.logradouro, '') as Logradouro,
            COALESCE(e.numero, '') as Numero,
            COALESCE(e.complemento, '') as Complemento,
            COALESCE(e.bairro, '') as Bairro,
            COALESCE(e.cep, '') as Cep,
            COALESCE(e.tipologradouro, '') as TipoLogradouro,
            '' as EnderecoLivre,
            COALESCE(SUM(CASE WHEN ex.naturezaoperacao = 'C' THEN ex.valoroperacao ELSE 0 END), 0) as TotCreditos,
            COALESCE(SUM(CASE WHEN ex.naturezaoperacao = 'D' THEN ex.valoroperacao ELSE 0 END), 0) as TotDebitos
        FROM manager.tb_pessoa p
        INNER JOIN manager.tb_pessoafisica pf ON pf.idpessoa = p.idpessoa
        INNER JOIN conta.tb_conta c ON c.idpessoa = p.idpessoa
        INNER JOIN conta.tb_extrato ex ON ex.idconta = c.idconta
        LEFT JOIN manager.tb_endereco e ON e.idpessoa = p.idpessoa AND e.situacao = '1'
        WHERE p.situacao = '1'
          AND c.situacao = '1'
          AND pf.cpf IS NOT NULL
          AND pf.cpf != ''
          AND LENGTH(TRIM(pf.cpf)) >= 11
          AND EXTRACT(YEAR FROM ex.dataoperacao) = :ano
          AND EXTRACT(MONTH FROM ex.dataoperacao) BETWEEN :mesInicial AND :mesFinal
        GROUP BY 
            p.idpessoa, p.documento, p.nome, pf.cpf, pf.nacionalidade, p.telefone, p.email,
            c.idconta, c.numeroconta, c.digitoconta, c.saldoatual,
            e.logradouro, e.numero, e.complemento, e.bairro, e.cep, e.tipologradouro
        ORDER BY p.idpessoa
        LIMIT :limit OFFSET :offset
        """;
    
    private static final String CALCULAR_TOTAIS_MOVIMENTACAO_SQL = """
        SELECT 
            COALESCE(SUM(CASE WHEN e.naturezaoperacao = 'C' THEN e.valoroperacao ELSE 0 END), 0) as TotCreditos,
            COALESCE(SUM(CASE WHEN e.naturezaoperacao = 'D' THEN e.valoroperacao ELSE 0 END), 0) as TotDebitos
        FROM conta.tb_extrato e
        WHERE e.idconta = :idConta
          AND EXTRACT(YEAR FROM e.dataoperacao) = :ano
          AND EXTRACT(MONTH FROM e.dataoperacao) BETWEEN :mesInicial AND :mesFinal
        """;
    
    public PessoaContaRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public List<DadosPessoaConta> buscarPessoasComContas(Integer ano, Integer mesInicial, Integer mesFinal, Integer limit, Integer offset) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("ano", ano)
                .addValue("mesInicial", mesInicial)
                .addValue("mesFinal", mesFinal)
                .addValue("limit", limit)
                .addValue("offset", offset);
        
        return jdbcTemplate.query(BUSCAR_PESSOAS_COM_CONTAS_SQL, params, pessoaContaRowMapper());
    }
    
    public TotaisMovimentacao calcularTotaisMovimentacao(Long idConta, Integer ano, Integer mesInicial, Integer mesFinal) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("idConta", idConta)
                .addValue("ano", ano)
                .addValue("mesInicial", mesInicial)
                .addValue("mesFinal", mesFinal);
        
        return jdbcTemplate.queryForObject(CALCULAR_TOTAIS_MOVIMENTACAO_SQL, params, totaisMovimentacaoRowMapper());
    }
    
    private RowMapper<DadosPessoaConta> pessoaContaRowMapper() {
        return (rs, rowNum) -> DadosPessoaConta.builder()
                .idPessoa(rs.getLong("IdPessoa"))
                .documento(getString(rs, "Documento"))
                .nome(getString(rs, "Nome"))
                .cpf(getString(rs, "Cpf"))
                .nacionalidade(getString(rs, "Nacionalidade", "BR"))
                .telefone(getString(rs, "Telefone"))
                .email(getString(rs, "Email"))
                .idConta(rs.getLong("IdConta"))
                .numeroConta(getString(rs, "NumeroConta"))
                .digitoConta(getString(rs, "DigitoConta"))
                .saldoAtual(getBigDecimal(rs, "SaldoAtual"))
                .logradouro(getString(rs, "Logradouro"))
                .numero(getString(rs, "Numero"))
                .complemento(getString(rs, "Complemento"))
                .bairro(getString(rs, "Bairro"))
                .cep(getString(rs, "Cep"))
                .tipoLogradouro(getString(rs, "TipoLogradouro"))
                .enderecoLivre(getString(rs, "EnderecoLivre"))
                .totCreditos(getBigDecimal(rs, "TotCreditos"))
                .totDebitos(getBigDecimal(rs, "TotDebitos"))
                .build();
    }
    
    private RowMapper<TotaisMovimentacao> totaisMovimentacaoRowMapper() {
        return (rs, rowNum) -> TotaisMovimentacao.builder()
                .totCreditos(getBigDecimal(rs, "TotCreditos"))
                .totDebitos(getBigDecimal(rs, "TotDebitos"))
                .build();
    }
    
    private String getString(java.sql.ResultSet rs, String columnName) {
        return getString(rs, columnName, "");
    }
    
    private String getString(java.sql.ResultSet rs, String columnName, String defaultValue) {
        try {
            String value = rs.getString(columnName);
            return rs.wasNull() ? defaultValue : (value != null ? value : defaultValue);
        } catch (Exception e) {
            return defaultValue;
        }
    }
    
    private BigDecimal getBigDecimal(java.sql.ResultSet rs, String columnName) {
        try {
            BigDecimal value = rs.getBigDecimal(columnName);
            return rs.wasNull() ? BigDecimal.ZERO : (value != null ? value : BigDecimal.ZERO);
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }
}
