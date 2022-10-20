package com.sketch.brain.backend.configurations;

import com.zaxxer.hikari.HikariDataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.SimpleDriverDataSource;

import javax.sql.DataSource;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
@PropertySource("classpath:application.yaml")
public class DataSourceConfiguration {

    private final Environment environment;

    @Bean(name = "dataSource")
    public DataSource dataSource() throws SQLException {

        SimpleDriverDataSource dataSource = new SimpleDriverDataSource();
        dataSource.setDriver(DriverManager.getDriver(this.environment.getProperty("spring.datasource.url")));
        dataSource.setUrl(this.environment.getProperty("spring.datasource.url"));
        dataSource.setUsername(this.environment.getProperty("spring.datasource.username"));
        dataSource.setPassword(this.environment.getProperty("spring.datasource.password"));
        Properties connectionProperties = new Properties();
        connectionProperties.setProperty("useUnicode","true");
        connectionProperties.setProperty("characterEncoding","UTF-8");
        connectionProperties.setProperty("prepareThreshold","0");
        dataSource.setConnectionProperties(connectionProperties);

        if(Objects.equals(environment.getProperty("spring.config.activate.on-profile"), "sketch-dev") ||
                Objects.equals(environment.getProperty("spring.config.activate.on-profile"), "sketch-prod")){
            return dataSource;
        }else return DataSourceBuilder.create().type(HikariDataSource.class).build();
    }
}
