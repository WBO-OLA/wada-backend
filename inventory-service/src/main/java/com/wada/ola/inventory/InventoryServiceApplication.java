package com.wada.ola.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.wada.ola.inventory", "com.wada.ola.common"})
@EntityScan(basePackages = {"com.wada.ola.inventory.entity", "com.wada.ola.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wada.ola.inventory.repository", "com.wada.ola.common.repository"})
public class InventoryServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
