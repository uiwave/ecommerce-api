package com.uiwave.ecommerce.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UpdateProfileRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        String fullName,

        @URL(message = "La imagen debe ser una URL válida")
        @Pattern(regexp = "^$|^https?://.*", message = "La imagen debe ser una URL http(s) válida")
        @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
        String imageUrl) {
}
