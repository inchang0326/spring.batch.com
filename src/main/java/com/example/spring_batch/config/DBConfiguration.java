package com.example.spring_batch.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class DBConfiguration {

    @Value("${spring.primary.datasource.driver-class-name}")
    private String primaryDriverClassName;
    @Value("${spring.primary.datasource.jdbc-url}")
    private String primaryJdbcUrl;
    @Value("${spring.primary.datasource.username}")
    private String primaryUsername;
    @Value("${spring.primary.datasource.password}")
    private String primaryPassword;
    @Value("${spring.primary.datasource.hikari.maximumPoolSize}")
    private int primaryHikariMaxPoolSize;

    @Value("${spring.read.datasource.driver-class-name}")
    private String readDriverClassName;
    @Value("${spring.read.datasource.jdbc-url}")
    private String readJdbcUrl;
    @Value("${spring.read.datasource.username}")
    private String readUsername;
    @Value("${spring.read.datasource.password}")
    private String readPassword;
    @Value("${spring.read.datasource.hikari.maximumPoolSize}")
    private int readHikariMaxPoolSize;

    @Value("${spring.write.datasource.driver-class-name}")
    private String writeDriverClassName;
    @Value("${spring.write.datasource.jdbc-url}")
    private String writeJdbcUrl;
    @Value("${spring.write.datasource.username}")
    private String writeUsername;
    @Value("${spring.write.datasource.password}")
    private String writePassword;
    @Value("${spring.write.datasource.hikari.maximumPoolSize}")
    private int writeHikariMaxPoolSize;

    @Primary
    @Bean
    public DataSource primaryDataSource() {
        return new HikariDataSource(getHikariConfig(primaryDriverClassName, primaryJdbcUrl, primaryUsername, primaryPassword, primaryHikariMaxPoolSize));
    }

    @Bean
    public DataSource readDataSource() {
        return new HikariDataSource(getHikariConfig(readDriverClassName, readJdbcUrl, readUsername, readPassword, readHikariMaxPoolSize));
    }

    @Bean
    public DataSource writeDataSource() {
        return new HikariDataSource(getHikariConfig(writeDriverClassName, writeJdbcUrl, writeUsername, writePassword, writeHikariMaxPoolSize));
    }

    private HikariConfig getHikariConfig(String driverClassName, String jdbcUrl, String username, String password, int hikariMaxPoolSize) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driverClassName);
        hikariConfig.setJdbcUrl(jdbcUrl);
        hikariConfig.setUsername(username);
        hikariConfig.setPassword(password);
        hikariConfig.setMaximumPoolSize(hikariMaxPoolSize);
        return hikariConfig;
    }
}