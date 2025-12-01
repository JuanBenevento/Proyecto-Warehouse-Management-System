package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.service.UserService;
import com.juanbenevento.wms.domain.model.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')") // <--- ¡CANDADO DE SEGURIDAD!
    public ResponseEntity<List<UserService.UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserService.UserResponse> createUser(@RequestBody UserService.CreateUserRequest request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/roles")
    public ResponseEntity<Role[]> getRoles() {
        return ResponseEntity.ok(Role.values());
    }
}