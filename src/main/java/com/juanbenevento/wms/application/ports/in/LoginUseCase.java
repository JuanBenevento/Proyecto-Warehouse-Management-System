package com.juanbenevento.wms.application.ports.in;

public interface LoginUseCase {
    AuthResponse login(LoginCommand command);

    // DTOs (Records)
    record LoginCommand(String username, String password) {}
    record AuthResponse(String token) {}
}