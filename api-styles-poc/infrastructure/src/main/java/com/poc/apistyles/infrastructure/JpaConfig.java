package com.poc.apistyles.infrastructure.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.poc.apistyles.infrastructure.persistence.repository")
@EnableTransactionManagement
public class JpaConfig {
}
