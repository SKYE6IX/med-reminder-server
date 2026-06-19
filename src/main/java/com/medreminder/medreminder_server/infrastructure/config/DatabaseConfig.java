package com.medreminder.medreminder_server.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Profile("main")
public class DatabaseConfig {

    private final Environment env;

    @Autowired
    public DatabaseConfig(Environment env) {
        this.env = env;
    }

    @Bean
    public HikariDataSource dataSource(){
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(env.getProperty("database.url"));
        config.setUsername(env.getProperty("database.username"));
        config.setPassword(env.getProperty("database.password"));
        config.setDriverClassName("org.postgresql.Driver");

        Properties props = new Properties();
        props.setProperty("sslmode", "verify-full");
        props.setProperty("sslrootcert", env.getProperty("pgsslrootcert"));
        config.setDataSourceProperties(props);

        return new HikariDataSource(config);
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway(DataSource dataSource) {
        return Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:db/migration")
                .load();
    }

}
