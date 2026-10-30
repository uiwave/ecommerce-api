package com.uiwave.ecommerce.setting.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record SiteSettingRequest(
        @NotBlank(message = "El nombre del sitio es obligatorio")
        @Size(max = 120, message = "El nombre del sitio no puede superar los 120 caracteres")
        String siteName,

        @Size(max = 500, message = "La URL del logo no puede superar los 500 caracteres")
        String logoUrl,

        @Email(message = "El email de contacto no tiene un formato válido")
        @Size(max = 150, message = "El email de contacto no puede superar los 150 caracteres")
        String contactEmail,

        @Size(max = 30, message = "El teléfono de contacto no puede superar los 30 caracteres")
        String contactPhone,

        @NotBlank(message = "La moneda es obligatoria")
        @Pattern(regexp = "^(PEN|USD|EUR)$", message = "La moneda debe ser PEN, USD o EUR")
        String currency,

        @NotNull(message = "El umbral de stock bajo es obligatorio")
        @Min(value = 0, message = "El umbral de stock bajo no puede ser negativo")
        Integer lowStockThreshold,

        @DecimalMin(value = "0.0", message = "El umbral de envío gratis no puede ser negativo")
        @Digits(integer = 8, fraction = 2, message = "El umbral de envío gratis admite hasta 2 decimales")
        BigDecimal freeShippingThreshold,

        @NotNull(message = "La tasa de impuesto es obligatoria")
        @DecimalMin(value = "0.0", message = "La tasa de impuesto no puede ser negativa")
        @DecimalMax(value = "100.0", message = "La tasa de impuesto no puede superar el 100%")
        @Digits(integer = 3, fraction = 2, message = "La tasa de impuesto admite hasta 2 decimales")
        BigDecimal taxRate,

        @NotNull(message = "La tarifa de envío es obligatoria")
        @DecimalMin(value = "0.0", message = "La tarifa de envío no puede ser negativa")
        @Digits(integer = 8, fraction = 2, message = "La tarifa de envío admite hasta 2 decimales")
        BigDecimal shippingFlatRate) {
}
