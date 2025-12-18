package com.juanbenevento.wms.application.service;

import com.juanbenevento.wms.application.ports.in.usecases.LoginUseCase;
import com.juanbenevento.wms.application.ports.out.UserRepositoryPort;
import com.juanbenevento.wms.domain.model.User;
import com.juanbenevento.wms.infrastructure.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService implements LoginUseCase {
    private final AuthenticationManager authenticationManager;
    private final UserRepositoryPort userRepositoryPort;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginCommand command) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(command.username(), command.password())
        );

        User user = userRepositoryPort.findByUsername(command.username())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado (Error de integridad)"));

        String jwtToken = jwtService.generateToken(user);

        return new AuthResponse(jwtToken);
    }
}