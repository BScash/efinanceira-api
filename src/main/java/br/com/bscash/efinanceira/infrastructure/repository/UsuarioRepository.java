package br.com.bscash.efinanceira.infrastructure.repository;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
public class UsuarioRepository {
    
    private final NamedParameterJdbcTemplate jdbcTemplate;
    
    private static final String BUSCAR_POR_LOGIN_OU_CPF_SQL = """
        SELECT 
            u.idusuario as IdUsuario,
            u.login as Login,
            u.senha as Senha,
            u.cpf as Cpf,
            u.nome as Nome,
            u.email as Email,
            u.celular as Celular,
            CASE 
                WHEN COALESCE(u.situacao, '1') = '1' THEN true
                ELSE false
            END as Ativo,
            CASE 
                WHEN u.tipobloqueio IS NOT NULL AND u.tipobloqueio != '' THEN true
                ELSE false
            END as Bloqueado
        FROM controleacesso.tb_usuario u
        WHERE (UPPER(TRIM(u.login)) = UPPER(TRIM(:loginOuCpf)) 
               OR REPLACE(REPLACE(REPLACE(u.cpf, '.', ''), '-', ''), ' ', '') = REPLACE(REPLACE(REPLACE(:loginOuCpf, '.', ''), '-', ''), ' ', ''))
          AND (u.situacao IS NULL OR u.situacao = '1')
        LIMIT 1
        """;
    
    public UsuarioRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    public Optional<UsuarioEntity> buscarPorLoginOuCpf(String loginOuCpf) {
        MapSqlParameterSource params = new MapSqlParameterSource()
                .addValue("loginOuCpf", loginOuCpf);
        
        try {
            UsuarioEntity usuario = jdbcTemplate.queryForObject(
                    BUSCAR_POR_LOGIN_OU_CPF_SQL, 
                    params, 
                    new UsuarioRowMapper()
            );
            return Optional.ofNullable(usuario);
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    private static class UsuarioRowMapper implements RowMapper<UsuarioEntity> {
        @Override
        public UsuarioEntity mapRow(ResultSet rs, int rowNum) throws SQLException {
            UsuarioEntity usuario = new UsuarioEntity();
            usuario.setId(rs.getLong("IdUsuario"));
            usuario.setLogin(rs.getString("Login"));
            usuario.setSenha(rs.getString("Senha"));
            usuario.setCpf(rs.getString("Cpf"));
            usuario.setNome(rs.getString("Nome"));
            usuario.setEmail(rs.getString("Email"));
            usuario.setCelular(rs.getString("Celular"));
            usuario.setAtivo(rs.getBoolean("Ativo"));
            usuario.setBloqueado(rs.getBoolean("Bloqueado"));
            return usuario;
        }
    }
    
    public static class UsuarioEntity {
        private Long id;
        private String login;
        private String senha;
        private String cpf;
        private String nome;
        private String email;
        private String celular;
        private boolean ativo;
        private boolean bloqueado;
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getLogin() {
            return login;
        }
        
        public void setLogin(String login) {
            this.login = login;
        }
        
        public String getSenha() {
            return senha;
        }
        
        public void setSenha(String senha) {
            this.senha = senha;
        }
        
        public String getCpf() {
            return cpf;
        }
        
        public void setCpf(String cpf) {
            this.cpf = cpf;
        }
        
        public String getNome() {
            return nome;
        }
        
        public void setNome(String nome) {
            this.nome = nome;
        }
        
        public String getEmail() {
            return email;
        }
        
        public void setEmail(String email) {
            this.email = email;
        }
        
        public String getCelular() {
            return celular;
        }
        
        public void setCelular(String celular) {
            this.celular = celular;
        }
        
        public boolean isAtivo() {
            return ativo;
        }
        
        public void setAtivo(boolean ativo) {
            this.ativo = ativo;
        }
        
        public boolean isBloqueado() {
            return bloqueado;
        }
        
        public void setBloqueado(boolean bloqueado) {
            this.bloqueado = bloqueado;
        }
    }
}
