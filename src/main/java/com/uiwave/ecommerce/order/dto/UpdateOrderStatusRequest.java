package com.uiwave.ecommerce.order.dto;

import com.uiwave.ecommerce.order.model.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(
        @NotNull(message = "El estado es obligatorio")
        OrderStatus status) {
}
