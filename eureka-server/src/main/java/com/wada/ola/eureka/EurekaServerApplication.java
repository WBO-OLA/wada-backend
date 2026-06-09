package com.wada.ola.eureka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {

    private static final Logger log = LoggerFactory.getLogger(EurekaServerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }

    @Bean
    ApplicationRunner startupLogger(Environment env) {
        return args -> {
            String port = env.getProperty("server.port", "8761");
            log.info("==============================================");
            log.info("  Eureka Server started on port {}", port);
            log.info("  Dashboard : http://localhost:{}", port);
            log.info("  Health    : http://localhost:{}/actuator/health", port);
            log.info("==============================================");
        };
    }
}
