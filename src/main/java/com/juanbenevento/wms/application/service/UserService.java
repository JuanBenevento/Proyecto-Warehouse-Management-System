package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.usecases.ManageUserUseCase;
import com.juanbenevento.wms.application.ports.out.UserRepositoryPort;
import com.juanbenevento.wms.domain.model.Role;
import com.juanbenevento.wms.domain.model.User;
import com.juanbenevento.wms.infrastructure.config.tenant.TenantContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements ManageUserUseCase {

    // Inyección de dependencias por interfaz (Puerto)
    private final UserRepositoryPort userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserResponse> getAllUsers() {
        // Convertimos la lista de Dominios (User) a DTOs (UserResponse)
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        // 1. Validar si ya existe
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe.");
        }

        // 2. Obtener la empresa actual (Tenant)
        String currentTenant = TenantContext.getTenantId();
        if (currentTenant == null) {
            throw new IllegalStateException("Error de seguridad: No se puede crear usuario sin contexto de empresa.");
        }

        // 3. Crear Objeto de Dominio (Puro)
        User user = new User(
                null, // El ID se genera en base de datos
                request.username(),
                passwordEncoder.encode(request.password()), // Encriptamos aquí
                request.role(),
                currentTenant
        );

        // 4. Guardar a través del Puerto
        // El adaptador se encargará de convertir User -> UserEntity
        User savedUser = userRepository.save(user);

        return mapToResponse(savedUser);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    @Override
    public Role[] getAvailableRoles() {
        return Role.values();
    }

    // --- Mapper Privado ---
    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getRole()
        );
    }
}