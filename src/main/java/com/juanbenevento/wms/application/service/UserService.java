package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.domain.model.Role;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.UserEntity;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public UserResponse createUser(CreateUserRequest request) {
        if (userRepository.findByUsername(request.username).isPresent()) {
            throw new IllegalArgumentException("El usuario ya existe");
        }

        UserEntity user = UserEntity.builder()
                .username(request.username)
                .password(passwordEncoder.encode(request.password)) // ¡Encriptar siempre!
                .role(request.role)
                .build();

        UserEntity saved = userRepository.save(user);
        return mapToResponse(saved);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(UserEntity user) {
        return new UserResponse(user.getId(), user.getUsername(), user.getRole());
    }

    // DTOs
    public record UserResponse(Long id, String username, Role role) {}
    public record CreateUserRequest(String username, String password, Role role) {}
}