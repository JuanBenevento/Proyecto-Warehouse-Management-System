package com.juanbenevento.wms.infrastructure.config;

import com.juanbenevento.wms.domain.model.Role;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.UserEntity;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @EventListener(ApplicationReadyEvent.class)
    public void initData() {
        log.info("Verificando datos iniciales...");

        try {
            if (userRepository.count() == 0) {
                log.info("Base de datos de usuarios vacía. Creando Super Admin...");

                UserEntity admin = UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("123")) // Contraseña simple para dev
                        .role(Role.ADMIN)
                        .build();

                userRepository.save(admin);
                log.info("uper Admin creado con éxito. Usuario: 'admin' / Pass: '123'");
            } else {
                log.info("Ya existen usuarios en la base de datos. Saltando inicialización.");
            }
        } catch (Exception e) {
            log.error("Error al inicializar datos: " + e.getMessage());
        }
    }
}