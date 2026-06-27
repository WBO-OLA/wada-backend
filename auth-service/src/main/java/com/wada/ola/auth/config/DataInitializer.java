package com.wada.ola.auth.config;

import com.wada.ola.auth.entity.User;
import com.wada.ola.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@moms.mil");
            admin.setPassword(passwordEncoder.encode("Admin@1234"));
            admin.setRole(User.Role.ADMIN);
            userRepository.save(admin);
            log.info("Default admin user created — username: admin / password: Admin@1234");
        }
    }
}
