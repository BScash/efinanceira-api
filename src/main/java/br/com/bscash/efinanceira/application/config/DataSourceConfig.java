package br.com.bscash.efinanceira.application.config;

import br.com.bscash.efinanceira.infrastructure.util.Criptografia;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String encryptedPassword;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${spring.datasource.hikari.maximum-pool-size:20}")
    private int maximumPoolSize;

    @Value("${spring.datasource.hikari.minimum-idle:5}")
    private int minimumIdle;

    @Value("${spring.datasource.hikari.connection-timeout:30000}")
    private long connectionTimeout;

    @Value("${spring.datasource.hikari.idle-timeout:600000}")
    private long idleTimeout;

    @Value("${spring.datasource.hikari.max-lifetime:1800000}")
    private long maxLifetime;

    @Value("${spring.datasource.hikari.leak-detection-threshold:60000}")
    private long leakDetectionThreshold;

    @Bean
    @Primary
    public DataSource dataSource() {
        logger.info("Configurando DataSource com descriptografia de senha...");
        
        try {
            Criptografia criptografia = new Criptografia();
            String decryptedPassword = criptografia.decrypt(encryptedPassword);
            
            if (decryptedPassword == null) {
                logger.warn("Falha ao descriptografar senha. Tentando usar senha original (pode estar em texto plano).");
                decryptedPassword = encryptedPassword;
            } else {
                logger.info("Senha descriptografada com sucesso.");
            }
            
            HikariConfig config = new HikariConfig();
            config.setJdbcUrl(url);
            config.setUsername(username);
            config.setPassword(decryptedPassword);
            config.setDriverClassName(driverClassName);
            config.setMaximumPoolSize(maximumPoolSize);
            config.setMinimumIdle(minimumIdle);
            config.setConnectionTimeout(connectionTimeout);
            config.setIdleTimeout(idleTimeout);
            config.setMaxLifetime(maxLifetime);
            config.setLeakDetectionThreshold(leakDetectionThreshold);
            
            return new HikariDataSource(config);
            
        } catch (RuntimeException e) {
            throw new IllegalStateException("Falha ao configurar DataSource. Verifique as configurações de conexão e criptografia.", e);
        } catch (Exception e) {
            throw new IllegalStateException("Falha inesperada ao configurar DataSource. Verifique as configurações de conexão e criptografia.", e);
        }
    }
}
