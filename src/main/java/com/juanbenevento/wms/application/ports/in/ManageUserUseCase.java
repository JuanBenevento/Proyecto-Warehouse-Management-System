package com.juanbenevento.wms.application.ports.in;

import com.juanbenevento.wms.domain.model.Role;
import java.util.List;

public interface ManageUserUseCase {
    List<UserResponse> getAllUsers();
    UserResponse createUser(CreateUserRequest request);
    void deleteUser(Long id);
    Role[] getAvailableRoles();

    // DTOs dentro de la interfaz
    record UserResponse(Long id, String username, Role role) {}
    record CreateUserRequest(String username, String password, Role role) {}
}