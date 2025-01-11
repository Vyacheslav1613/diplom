package ru.netology.diplom.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DataSourceConfig {

    private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setUsername("postgres");
        dataSource.setPassword("123");
        try (Connection connection = dataSource.getConnection()) {
            logger.info("Соединение успешно установлено");
        } catch (SQLException e) {
            logger.error("Ошибка при установлении соединения", e);
        }
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }
}
