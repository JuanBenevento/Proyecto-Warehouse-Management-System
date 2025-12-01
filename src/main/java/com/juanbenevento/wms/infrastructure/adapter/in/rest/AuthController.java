package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.domain.model.Role;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.UserEntity;
import com.juanbenevento.wms.infrastructure.adapter.out.persistence.UserRepository;
import com.juanbenevento.wms.infrastructure.config.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "0. Autenticación", description = "Obtención de Tokens JWT.")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Operation(summary = "Iniciar Sesión", description = "Devuelve el Token JWT para usar en el Header 'Authorization'.")
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username, request.password)
        );
        var user = userRepository.findByUsername(request.username).orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(jwtToken));
    }

    public record LoginRequest(
            @Schema(example = "admin")
            String username,
            @Schema(example = "123")
            String password
    ) {}
    public record AuthResponse(String token) {}
}