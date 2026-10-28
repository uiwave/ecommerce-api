package com.uiwave.ecommerce.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record RegisterRequest(

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email no tiene un formato válido")
        @Size(max = 150, message = "El email no puede superar los 150 caracteres")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres")
        String password,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        String fullName,

        @URL(message = "La imagen debe ser una URL válida")
        @Pattern(regexp = "^$|^https?://.*", message = "La imagen debe ser una URL http(s) válida")
        @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
        String imageUrl) {
}
