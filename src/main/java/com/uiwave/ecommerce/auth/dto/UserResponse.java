package com.uiwave.ecommerce.auth.dto;

import com.uiwave.ecommerce.user.model.Role;
import com.uiwave.ecommerce.user.model.User;

import java.time.Instant;

public record UserResponse(
        Long id,
        String email,
        String fullName,
        Role role,
        String imageUrl,
        Instant createdAt) {

    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole(),
                user.getImageUrl(),
                user.getCreatedAt());
    }
}
