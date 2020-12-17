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
public class FlipDataBaseConfiguration {

    @Bean
    @ConfigurationProperties("flip")
    public DataSourceProperties fDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "fDataSource")
    public DataSource fDataSource() {
        DataSourceProperties dataSourceProperties = fDataSourceProperties();
        log.info("hchc datasource:{}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "fTransactionManager")
    @Resource
    public PlatformTransactionManager fTransactionManager(DataSource fDataSource) {
        return new DataSourceTransactionManager(fDataSource);
    }

    @Bean(name = "fJdbcTemplate")
    public JdbcTemplate fJdbcTemplate() {
        return new JdbcTemplate(fDataSource());
    }

}
