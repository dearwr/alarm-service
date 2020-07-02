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
public class HcHcDataBaseConfiguration {

    @Bean
    @ConfigurationProperties("hchc")
    public DataSourceProperties hDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean(name = "hDataSource")
    public DataSource hDataSource() {
        DataSourceProperties dataSourceProperties = hDataSourceProperties();
        log.info("hchc datasource:{}", dataSourceProperties.getUrl());
        return dataSourceProperties.initializeDataSourceBuilder().build();
    }

    @Bean(name = "hTransactionManager")
    @Resource
    public PlatformTransactionManager hTransactionManager(DataSource hDataSource) {
        return new DataSourceTransactionManager(hDataSource);
    }

    @Bean(name = "hJdbcTemplate")
    public JdbcTemplate hJdbcTemplate() {
        return new JdbcTemplate(hDataSource());
    }

}
