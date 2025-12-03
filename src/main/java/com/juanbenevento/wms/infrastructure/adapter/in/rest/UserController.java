package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.ManageUserUseCase;
import com.juanbenevento.wms.domain.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "5. Gestión de Usuarios (Admin)", description = "Administración de personal y roles dentro de la empresa.")
public class UserController {
    private final ManageUserUseCase manageUserUseCase;

    @Operation(summary = "Listar usuarios", description = "Devuelve todos los usuarios de la empresa actual.")
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<List<ManageUserUseCase.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(manageUserUseCase.getAllUsers());
    }

    @Operation(summary = "Crear usuario", description = "Registra un nuevo empleado (Admin u Operador).")
    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<ManageUserUseCase.UserResponse> createUser(@RequestBody ManageUserUseCase.CreateUserRequest request) {
        return ResponseEntity.ok(manageUserUseCase.createUser(request));
    }

    @Operation(summary = "Eliminar usuario", description = "Da de baja un usuario por ID.")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        manageUserUseCase.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Obtener roles", description = "Lista los roles disponibles para asignar.")
    @GetMapping("/roles")
    public ResponseEntity<Role[]> getRoles() {
        return ResponseEntity.ok(manageUserUseCase.getAvailableRoles());
    }
}