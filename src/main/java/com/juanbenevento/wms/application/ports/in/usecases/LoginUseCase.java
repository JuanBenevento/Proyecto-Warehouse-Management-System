package com.juanbenevento.wms.application.ports.in.usecases;

public interface LoginUseCase {
    AuthResponse login(LoginCommand command);

    record LoginCommand(String username, String password) {}
    record AuthResponse(String token) {}
}