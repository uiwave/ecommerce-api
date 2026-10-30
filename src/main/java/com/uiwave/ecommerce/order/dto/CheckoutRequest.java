package com.uiwave.ecommerce.order.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
        @NotBlank(message = "El nombre de quien recibe es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        String fullName,

        @NotBlank(message = "El teléfono es obligatorio")
        @Size(max = 30, message = "El teléfono no puede superar los 30 caracteres")
        String phone,

        @NotBlank(message = "La dirección es obligatoria")
        @Size(max = 200, message = "La dirección no puede superar los 200 caracteres")
        String address,

        @NotBlank(message = "La ciudad es obligatoria")
        @Size(max = 80, message = "La ciudad no puede superar los 80 caracteres")
        String city,

        @NotBlank(message = "El código postal es obligatorio")
        @Size(max = 12, message = "El código postal no puede superar los 12 caracteres")
        String postalCode,

        @NotBlank(message = "El país es obligatorio")
        @Size(max = 60, message = "El país no puede superar los 60 caracteres")
        String country) {
}
