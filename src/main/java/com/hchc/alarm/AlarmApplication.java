package com.hchc.alarm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.JdbcTemplateAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.sql.DataSource;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JdbcTemplateAutoConfiguration.class})
@Slf4j
public class AlarmApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlarmApplication.class, args);
	}

	@Bean
	@ConfigurationProperties("hchc")
	public DataSourceProperties hDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource hDataSource() {
		DataSourceProperties dataSourceProperties = hDataSourceProperties();
		log.info("hchc datasource:{}", dataSourceProperties.getUrl());
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean
	public JdbcTemplate hJdbcTemplate() {
		return new JdbcTemplate(hDataSource());
	}

	@Bean
	@ConfigurationProperties("flip")
	public DataSourceProperties fDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource fDataSource() {
		DataSourceProperties dataSourceProperties = fDataSourceProperties();
		log.info("flip datasource:{}", dataSourceProperties.getUrl());
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean
	public JdbcTemplate fJdbcTemplate() {
		return new JdbcTemplate(fDataSource());
	}

	@Bean
	@Resource
	public PlatformTransactionManager hTransactionManager(DataSource hDataSource) {
		return new DataSourceTransactionManager(hDataSource);
	}

	@Bean
	@ConfigurationProperties("rocket")
	public DataSourceProperties rDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource rDataSource() {
		DataSourceProperties dataSourceProperties = rDataSourceProperties();
		log.info("rocket datasource:{}", dataSourceProperties.getUrl());
		return dataSourceProperties.initializeDataSourceBuilder().build();
	}

	@Bean
	public JdbcTemplate rJdbcTemplate() {
		return new JdbcTemplate(rDataSource());
	}

	@Bean
	@Resource
	public PlatformTransactionManager rTransactionManager(DataSource rDataSource) {
		return new DataSourceTransactionManager(rDataSource);
	}

	@Bean
	public RestTemplate restTemplate(ClientHttpRequestFactory factory) {
		return new RestTemplate(factory);
	}

	@Bean
	public ClientHttpRequestFactory simpleClientHttpRequestFactory() {
		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
		factory.setReadTimeout(5000);//单位为ms
		factory.setConnectTimeout(5000);//单位为ms
		return factory;
	}
}
