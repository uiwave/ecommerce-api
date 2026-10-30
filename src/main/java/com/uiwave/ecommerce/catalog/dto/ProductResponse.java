package com.uiwave.ecommerce.catalog.dto;

import com.uiwave.ecommerce.catalog.model.Product;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductResponse(
        Long id,
        String name,
        String slug,
        String description,
        BigDecimal price,
        BigDecimal comparePrice,
        int stock,
        String sku,
        Long categoryId,
        String categoryName,
        String imageUrl,
        boolean active,
        Instant createdAt) {

    public static ProductResponse from(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getSlug(),
                product.getDescription(),
                product.getPrice(),
                product.getComparePrice(),
                product.getStock(),
                product.getSku(),
                product.getCategory().getId(),
                product.getCategory().getName(),
                product.getImageUrl(),
                product.isActive(),
                product.getCreatedAt());
    }
}
