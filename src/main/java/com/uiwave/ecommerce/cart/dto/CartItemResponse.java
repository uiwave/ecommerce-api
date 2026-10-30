package com.uiwave.ecommerce.cart.dto;

import com.uiwave.ecommerce.cart.model.CartItem;
import com.uiwave.ecommerce.catalog.model.Product;

import java.math.BigDecimal;
import java.math.RoundingMode;

public record CartItemResponse(
        Long id,
        Long productId,
        String name,
        String slug,
        String imageUrl,
        BigDecimal unitPrice,
        int quantity,
        int stock,
        BigDecimal subtotal) {

    public static CartItemResponse from(CartItem item) {
        Product product = item.getProduct();
        BigDecimal subtotal = product.getPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()))
                .setScale(2, RoundingMode.HALF_UP);

        return new CartItemResponse(
                item.getId(),
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getImageUrl(),
                product.getPrice(),
                item.getQuantity(),
                product.getStock(),
                subtotal);
    }
}
