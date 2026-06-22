package com.wada.ola.personnel;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.wada.ola.personnel", "com.wada.ola.common"})
public class PersonnelServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(PersonnelServiceApplication.class, args);
    }
}
