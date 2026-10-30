package com.uiwave.ecommerce.cart;

import com.uiwave.ecommerce.cart.dto.AddCartItemRequest;
import com.uiwave.ecommerce.cart.dto.CartResponse;
import com.uiwave.ecommerce.cart.dto.UpdateCartItemRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@Tag(name = "Carrito", description = "Carrito del usuario autenticado")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "Carrito actual")
    @GetMapping
    public CartResponse getCart(Authentication authentication) {
        return cartService.getCart(authentication.getName());
    }

    @Operation(summary = "Agregar producto al carrito")
    @PostMapping("/items")
    public CartResponse addItem(
            Authentication authentication,
            @Valid @RequestBody AddCartItemRequest request) {
        return cartService.addItem(authentication.getName(), request);
    }

    @Operation(summary = "Actualizar cantidad de un ítem")
    @PatchMapping("/items/{itemId}")
    public CartResponse updateItem(
            Authentication authentication,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(authentication.getName(), itemId, request);
    }

    @Operation(summary = "Quitar un ítem del carrito")
    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(
            Authentication authentication,
            @PathVariable Long itemId) {
        return cartService.removeItem(authentication.getName(), itemId);
    }

    @Operation(summary = "Vaciar el carrito")
    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(Authentication authentication) {
        cartService.clear(authentication.getName());
    }
}
