package com.uiwave.ecommerce.auth.dto;

import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegisterRequestValidationTest {

    private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    @Test
    void acceptsHttpsImageUrl() {
        RegisterRequest request = new RegisterRequest(
                "cliente@test.com", "Cliente123!", "Cliente Test",
                "https://cdn.uiwave.com/avatars/cliente1.png");

        assertTrue(validator.validate(request).isEmpty(),
                () -> "Errores: " + validator.validate(request));
    }

    @Test
    void acceptsHttpImageUrl() {
        RegisterRequest request = new RegisterRequest(
                "cliente@test.com", "Cliente123!", "Cliente Test",
                "http://cdn.uiwave.com/avatars/cliente1.png");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void acceptsNullImageUrl() {
        RegisterRequest request = new RegisterRequest(
                "cliente@test.com", "Cliente123!", "Cliente Test", null);

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void acceptsEmptyImageUrl() {
        RegisterRequest request = new RegisterRequest(
                "cliente@test.com", "Cliente123!", "Cliente Test", "");

        assertTrue(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsInvalidImageUrl() {
        RegisterRequest request = new RegisterRequest(
                "cliente@test.com", "Cliente123!", "Cliente Test", "esto-no-es-una-url");

        assertFalse(validator.validate(request).isEmpty());
    }

    @Test
    void rejectsNonHttpProtocol() {
        RegisterRequest request = new RegisterRequest(
                "cliente@test.com", "Cliente123!", "Cliente Test", "ftp://cdn.uiwave.com/avatar.png");

        assertFalse(validator.validate(request).isEmpty());
    }
}
