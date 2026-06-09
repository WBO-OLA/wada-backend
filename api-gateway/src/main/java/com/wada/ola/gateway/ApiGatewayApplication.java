package com.wada.ola.gateway;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class ApiGatewayApplication {

    private static final Logger log = LoggerFactory.getLogger(ApiGatewayApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    ApplicationRunner startupLogger(Environment env) {
        return args -> {
            String port = env.getProperty("server.port", "8080");
            log.info("==============================================");
            log.info("  API Gateway started on port {}", port);
            log.info("  Entry point : http://localhost:{}/api/**", port);
            log.info("  Health      : http://localhost:{}/actuator/health", port);
            log.info("==============================================");
        };
    }
}
