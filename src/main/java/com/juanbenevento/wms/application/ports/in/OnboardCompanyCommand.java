package com.juanbenevento.wms.application.ports.in;

public record OnboardCompanyCommand(
        String companyName,
        String companyId,
        String adminEmail,
        String adminUsername,
        String adminPassword
) {
    public OnboardCompanyCommand {
        if (companyId == null || companyId.isBlank()) throw new IllegalArgumentException("ID de empresa requerido");
        if (adminUsername == null || adminUsername.isBlank()) throw new IllegalArgumentException("Usuario Admin requerido");
    }
}