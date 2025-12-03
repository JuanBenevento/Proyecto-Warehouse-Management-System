package com.juanbenevento.wms.infrastructure.adapter.in.rest;

import com.juanbenevento.wms.application.ports.in.ManageSaaSUseCase;
import com.juanbenevento.wms.application.ports.in.command.OnboardCompanyCommand;
import com.juanbenevento.wms.domain.model.Tenant;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/saas")
@RequiredArgsConstructor
@Tag(name = "0. Panel SaaS (Super Admin)", description = "Gestión de clientes y suscripciones")
public class SaaSController {
    private final ManageSaaSUseCase saasUseCase;

    @GetMapping("/tenants")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Listar Empresas", description = "Solo para el dueño del software.")
    public ResponseEntity<List<Tenant>> getAllTenants() {
        return ResponseEntity.ok(saasUseCase.getAllTenants());
    }

    @PostMapping("/onboarding")
    @PreAuthorize("hasAuthority('SUPER_ADMIN')")
    @Operation(summary = "Alta de Cliente", description = "Crea la empresa y su usuario Admin inicial.")
    public ResponseEntity<String> onboardCompany(@RequestBody OnboardRequest request) {

        OnboardCompanyCommand command = new OnboardCompanyCommand(
                request.companyName, request.companyId, request.adminEmail,
                request.adminUsername, request.adminPassword
        );

        saasUseCase.onboardNewCustomer(command);
        return ResponseEntity.ok("Empresa " + request.companyName + " registrada exitosamente.");
    }

    // DTO JSON
    public record OnboardRequest(
            String companyName,
            String companyId,
            String adminEmail,
            String adminUsername,
            String adminPassword
    ) {}
}