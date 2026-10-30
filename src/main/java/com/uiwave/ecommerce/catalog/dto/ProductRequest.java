package com.uiwave.ecommerce.catalog.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar los 120 caracteres")
        String name,

        @NotBlank(message = "La descripción es obligatoria")
        String description,

        @NotNull(message = "El precio es obligatorio")
        @DecimalMin(value = "0.0", message = "El precio no puede ser negativo")
        @Digits(integer = 8, fraction = 2, message = "El precio admite hasta 2 decimales")
        BigDecimal price,

        @DecimalMin(value = "0.0", message = "El precio de comparación no puede ser negativo")
        @Digits(integer = 8, fraction = 2, message = "El precio de comparación admite hasta 2 decimales")
        BigDecimal comparePrice,

        @NotNull(message = "El stock es obligatorio")
        @Min(value = 0, message = "El stock no puede ser negativo")
        Integer stock,

        @Size(max = 40, message = "El SKU no puede superar los 40 caracteres")
        String sku,

        @NotNull(message = "La categoría es obligatoria")
        Long categoryId,

        @Size(max = 500, message = "La URL de la imagen no puede superar los 500 caracteres")
        String imageUrl,

        Boolean active) {
}
