package com.uiwave.ecommerce.order.dto;

import com.uiwave.ecommerce.order.model.OrderStatus;
import com.uiwave.ecommerce.order.model.ShopOrder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        OrderStatus status,
        BigDecimal total,
        String fullName,
        String phone,
        String address,
        String city,
        String postalCode,
        String country,
        String userEmail,
        Instant createdAt,
        List<OrderItemResponse> items) {

    public static OrderResponse from(ShopOrder order) {
        return new OrderResponse(
                order.getId(),
                order.getStatus(),
                order.getTotal(),
                order.getFullName(),
                order.getPhone(),
                order.getAddress(),
                order.getCity(),
                order.getPostalCode(),
                order.getCountry(),
                order.getUser().getEmail(),
                order.getCreatedAt(),
                order.getItems().stream().map(OrderItemResponse::from).toList());
    }
}
