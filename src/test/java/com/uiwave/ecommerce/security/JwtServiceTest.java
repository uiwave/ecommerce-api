package com.uiwave.ecommerce.security;

import com.uiwave.ecommerce.user.model.Role;
import com.uiwave.ecommerce.user.model.User;
import io.jsonwebtoken.JwtException;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtServiceTest {

    private static final String SECRET = "secreto-de-pruebas-con-mas-de-32-caracteres-1234567890";
    private static final String OTHER_SECRET = "otro-secreto-distinto-con-mas-de-32-caracteres-0987654321";

    private final JwtService jwtService = new JwtService(
            new JwtProperties(SECRET, Duration.ofMinutes(15), Duration.ofDays(7)));

    private final User user = User.builder()
            .id(1L)
            .email("user@test.com")
            .password("irrelevante")
            .fullName("Usuario de Prueba")
            .role(Role.USER)
            .enabled(true)
            .build();

    @Test
    void generatesTokenWithSubjectAndValidSignature() {
        String token = jwtService.generateAccessToken(user);

        assertEquals("user@test.com", jwtService.extractUsername(token));
        assertTrue(jwtService.isValid(token, user));
    }

    @Test
    void rejectsTokenSignedWithAnotherKey() {
        String token = jwtService.generateAccessToken(user);
        JwtService otherService = new JwtService(
                new JwtProperties(OTHER_SECRET, Duration.ofMinutes(15), Duration.ofDays(7)));

        assertFalse(otherService.isValid(token, user));
    }

    @Test
    void rejectsExpiredToken() {
        JwtService expiredService = new JwtService(
                new JwtProperties(SECRET, Duration.ofSeconds(-5), Duration.ofDays(7)));
        String token = expiredService.generateAccessToken(user);

        assertFalse(expiredService.isValid(token, user));
    }

    @Test
    void rejectsMalformedToken() {
        assertFalse(jwtService.isValid("esto-no-es-un-jwt", user));
        assertThrows(JwtException.class, () -> jwtService.extractUsername("esto-no-es-un-jwt"));
    }

    @Test
    void rejectsTokenForAnotherUser() {
        String token = jwtService.generateAccessToken(user);
        User otherUser = User.builder()
                .id(2L)
                .email("otro@test.com")
                .password("irrelevante")
                .fullName("Otro Usuario")
                .role(Role.ADMIN)
                .enabled(true)
                .build();

        assertFalse(jwtService.isValid(token, otherUser));
    }
}
