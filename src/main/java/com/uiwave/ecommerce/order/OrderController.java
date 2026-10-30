package com.uiwave.ecommerce.order;

import com.uiwave.ecommerce.common.dto.PageResponse;
import com.uiwave.ecommerce.order.dto.CheckoutRequest;
import com.uiwave.ecommerce.order.dto.OrderResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Pedidos del usuario autenticado")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Crear pedido desde el carrito (checkout)")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse checkout(
            Authentication authentication,
            @Valid @RequestBody CheckoutRequest request) {
        return orderService.checkout(authentication.getName(), request);
    }

    @Operation(summary = "Mis pedidos")
    @GetMapping
    public PageResponse<OrderResponse> myOrders(
            Authentication authentication,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return orderService.listMine(authentication.getName(), page, size);
    }

    @Operation(summary = "Detalle de uno de mis pedidos")
    @GetMapping("/{orderId}")
    public OrderResponse myOrder(
            Authentication authentication,
            @PathVariable Long orderId) {
        return orderService.getMine(authentication.getName(), orderId);
    }
}
