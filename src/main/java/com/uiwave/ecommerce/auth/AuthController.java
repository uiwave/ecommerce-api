package com.uiwave.ecommerce.auth;

import com.uiwave.ecommerce.auth.dto.AuthResponse;
import com.uiwave.ecommerce.auth.dto.LoginRequest;
import com.uiwave.ecommerce.auth.dto.RefreshRequest;
import com.uiwave.ecommerce.auth.dto.RegisterRequest;
import com.uiwave.ecommerce.auth.dto.UserResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Registro, inicio de sesión y gestión de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Registrar un usuario nuevo (rol USER)")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @Operation(summary = "Iniciar sesión y obtener access token + refresh token")
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @Operation(summary = "Renovar el access token usando un refresh token (rotación)")
    @PostMapping("/refresh")
    public AuthResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request);
    }

    @Operation(summary = "Cerrar sesión revocando el refresh token")
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(Authentication authentication, @Valid @RequestBody RefreshRequest request) {
        authService.logout(authentication.getName(), request);
    }

    @Operation(summary = "Datos del usuario autenticado")
    @GetMapping("/me")
    public UserResponse me(Authentication authentication) {
        return authService.me(authentication.getName());
    }
}
