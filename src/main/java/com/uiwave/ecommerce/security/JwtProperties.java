package com.uiwave.ecommerce.security;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;

@Validated
@ConfigurationProperties(prefix = "app.jwt")
public record JwtProperties(
        @NotBlank
        @Size(min = 32, message = "JWT_SECRET debe tener al menos 32 caracteres")
        String secret,

        @NotNull
        Duration accessExpiration,

        @NotNull
        Duration refreshExpiration) {
}
