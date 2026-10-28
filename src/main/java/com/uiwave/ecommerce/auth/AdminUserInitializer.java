package com.uiwave.ecommerce.auth;

import com.uiwave.ecommerce.user.model.Role;
import com.uiwave.ecommerce.user.model.User;
import com.uiwave.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Locale;

/**
 * Crea el usuario administrador inicial a partir de ADMIN_EMAIL y ADMIN_PASSWORD.
 * Si las variables no están definidas, no hace nada.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminUserInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!StringUtils.hasText(adminEmail) || !StringUtils.hasText(adminPassword)) {
            log.info("ADMIN_EMAIL/ADMIN_PASSWORD no configurados: se omite la creación del administrador");
            return;
        }

        String email = adminEmail.trim().toLowerCase(Locale.ROOT);
        if (userRepository.existsByEmail(email)) {
            log.info("El usuario administrador {} ya existe", email);
            return;
        }

        User admin = User.builder()
                .email(email)
                .password(passwordEncoder.encode(adminPassword))
                .fullName("Administrador")
                .role(Role.ADMIN)
                .enabled(true)
                .build();
        userRepository.save(admin);
        log.info("Usuario administrador creado: {}", email);
    }
}
