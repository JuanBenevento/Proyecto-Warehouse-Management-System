package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.ManageSaaSUseCase; // <--- Interfaz
import com.juanbenevento.wms.application.ports.in.command.OnboardCompanyCommand; // <--- Comando
import com.juanbenevento.wms.application.ports.out.TenantRepositoryPort;
import com.juanbenevento.wms.application.ports.out.UserRepositoryPort;
import com.juanbenevento.wms.domain.model.Role;
import com.juanbenevento.wms.domain.model.Tenant;
import com.juanbenevento.wms.domain.model.TenantStatus;
import com.juanbenevento.wms.domain.model.User; // Dominio
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SaaSManagementService implements ManageSaaSUseCase {

    private final TenantRepositoryPort tenantRepository;
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    @Override
    @Transactional
    public void onboardNewCustomer(OnboardCompanyCommand command) {
        if (tenantRepository.existsById(command.companyId())) {
            throw new IllegalArgumentException("El ID de empresa '" + command.companyId() + "' ya existe.");
        }

        Tenant tenant = new Tenant(
                command.companyId().toUpperCase(),
                command.companyName(),
                TenantStatus.ACTIVE,
                command.adminEmail(),
                LocalDateTime.now()
        );
        tenantRepository.save(tenant);

        User adminUser = new User(
                null,
                command.adminUsername(),
                passwordEncoder.encode(command.adminPassword()),
                Role.ADMIN,
                command.companyId().toUpperCase()
        );
        userRepository.save(adminUser);
    }
}