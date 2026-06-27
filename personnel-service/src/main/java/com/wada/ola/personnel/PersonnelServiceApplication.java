package com.wada.ola.personnel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.wada.ola.personnel", "com.wada.ola.common"})
@EntityScan(basePackages = {"com.wada.ola.personnel.entity", "com.wada.ola.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wada.ola.personnel.repository", "com.wada.ola.common.repository"})
public class PersonnelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonnelServiceApplication.class, args);
    }
}
