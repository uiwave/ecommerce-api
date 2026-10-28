package com.uiwave.ecommerce.user.model;

/**
 * Roles disponibles en la aplicación. El prefijo ROLE_ que exige Spring Security
 * se agrega al construir las authorities del usuario.
 */
public enum Role {
    USER,
    ADMIN
}
