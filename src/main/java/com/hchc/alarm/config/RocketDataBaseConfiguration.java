package com.hchc.alarm.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.annotation.Resource;
import javax.sql.DataSource;

/**
 * @author wangrong
 * @date 2020-07-02
 */
@Configuration
@Slf4j
public class RocketDataBaseConfiguration {

    @Bean
    @ConfigurationProperties("rocket")
    public DataSourceProperties rDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "rDataSource")
    public DataSource rDataSource() {
        DataSourceProperties dataSourceProperties = rDataSourceProperties();
        log.info("rocket datasource:{}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "rJdbcTemplate")
    public JdbcTemplate rJdbcTemplate() {
        return new JdbcTemplate(rDataSource());
    }

    @Bean(name = "rTransactionManager")
    @Resource
    public PlatformTransactionManager rTransactionManager(DataSource rDataSource) {
        return new DataSourceTransactionManager(rDataSource);
    }

}
