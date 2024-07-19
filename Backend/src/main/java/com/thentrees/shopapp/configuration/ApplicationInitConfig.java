package com.thentrees.shopapp.configuration;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.thentrees.shopapp.models.Role;
import com.thentrees.shopapp.models.User;
import com.thentrees.shopapp.repositories.RoleRepository;
import com.thentrees.shopapp.repositories.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApplicationInitConfig {

    private final PasswordEncoder passwordEncoder;

    @Bean
    @ConditionalOnProperty(
            prefix = "spring",
            value = "datasource.driverClassName",
            havingValue = "com.mysql.cj.jdbc.Driver")
    ApplicationRunner applicationRunner(UserRepository userRepository, RoleRepository roleRepository) {
        log.info("Initializing application.....");
        return args -> {
            if (userRepository.findByPhoneNumber("0938749250").isEmpty()) {
                log.info("Creating default user.....");

                // init role
                Role userRole =
                        roleRepository.save(Role.builder().name(Role.ADMIN).build());

                Role adminRole =
                        roleRepository.save(Role.builder().name(Role.USER).build());

                roleRepository.save(userRole);
                roleRepository.save(adminRole);

                User user = User.builder()
                        .fullName("Admin")
                        .phoneNumber("0938749250")
                        .password(passwordEncoder.encode("admin"))
                        .active(true)
                        .role(adminRole)
                        .build();
                userRepository.save(user);
                log.warn("admin user has been created with default password: admin, please change it");
            }
            log.info("Application initialization completed .....");
        };
    }
}
