package com.uiwave.ecommerce.user.dto;

import com.uiwave.ecommerce.user.model.Role;

/** Cambios administrativos sobre una cuenta. Ambos campos son opcionales. */
public record UpdateUserRequest(Role role, Boolean enabled) {
}
