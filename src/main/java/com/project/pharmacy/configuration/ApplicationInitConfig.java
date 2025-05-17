package com.project.pharmacy.configuration;

import java.util.HashSet;
import java.util.Set;

import com.project.pharmacy.enums.Level;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.pharmacy.entity.Role;
import com.project.pharmacy.entity.User;
import com.project.pharmacy.repository.RoleRepository;
import com.project.pharmacy.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    RoleRepository roleRepository;

    PasswordEncoder passwordEncoder;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            // Tao Role
            createRoleIfNotExists("ADMIN", "Role for Admin", roleRepository);
            createRoleIfNotExists("EMPLOYEE", "Role for Employee", roleRepository);
            createRoleIfNotExists("DOCTOR", "Role for Doctor", roleRepository);
            createRoleIfNotExists("NURSE", "Role for Nurse", roleRepository);
            createRoleIfNotExists("USER", "Role for User", roleRepository);

            if (userRepository.findByUsername("admin").isEmpty()) {
                Set<Role> roles = new HashSet<>();
                roles.add(roleRepository.findByName("ADMIN").orElseThrow());
                User user = User.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("admin"))
                        .status(true)
                        .isVerified(true)
                        .roles(roles)
                        .level(Level.KIMCUONG)
                        .build();
                userRepository.save(user);
            }
        };
    }

    private void createRoleIfNotExists(String roleName, String description, RoleRepository roleRepository) {
        if (roleRepository.findByName(roleName).isEmpty()) {
            Role role = Role.builder().name(roleName).description(description).build();
            roleRepository.save(role);
        }
    }
}
