package com.wada.ola.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {"com.wada.ola.auth", "com.wada.ola.common"})
@EntityScan(basePackages = {"com.wada.ola.auth.entity", "com.wada.ola.common.entity"})
@EnableJpaRepositories(basePackages = {"com.wada.ola.auth.repository", "com.wada.ola.common.repository"})
public class AuthServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    @Bean
    ApplicationRunner startupLogger(Environment env) {
        return args -> {
            String port = env.getProperty("server.port", "8081");
            log.info("==============================================");
            log.info("  Auth Service started on port {}", port);
            log.info("  Register : POST http://localhost:{}/api/auth/register", port);
            log.info("  Login    : POST http://localhost:{}/api/auth/login", port);
            log.info("  Health   : http://localhost:{}/actuator/health", port);
            log.info("==============================================");
        };
    }
}
