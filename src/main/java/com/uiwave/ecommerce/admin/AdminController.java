package com.uiwave.ecommerce.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Administración", description = "Endpoints exclusivos para administradores")
public class AdminController {

    @Operation(summary = "Endpoint de prueba: solo accesible con rol ADMIN")
    @GetMapping("/hello")
    public Map<String, Object> hello(Authentication authentication) {
        return Map.of(
                "message", "Hola, administrador",
                "username", authentication.getName(),
                "timestamp", Instant.now());
    }
}
