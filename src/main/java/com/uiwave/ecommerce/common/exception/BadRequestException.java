package com.uiwave.ecommerce.common.exception;

/** Error de negocio recuperable: stock insuficiente, carrito vacío, etc. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
