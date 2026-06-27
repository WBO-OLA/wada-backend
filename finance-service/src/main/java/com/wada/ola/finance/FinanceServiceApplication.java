package com.wada.ola.finance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.wada.ola.finance", "com.wada.ola.common"})
@EntityScan(basePackages = {"com.wada.ola.finance.entity", "com.wada.ola.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wada.ola.finance.repository", "com.wada.ola.common.repository"})
public class FinanceServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(FinanceServiceApplication.class, args);
    }
}
