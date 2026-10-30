package com.uiwave.ecommerce.order;

import com.uiwave.ecommerce.common.dto.PageResponse;
import com.uiwave.ecommerce.order.dto.OrderResponse;
import com.uiwave.ecommerce.order.dto.UpdateOrderStatusRequest;
import com.uiwave.ecommerce.order.model.OrderStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administración - Pedidos", description = "Gestión de pedidos")
public class AdminOrderController {

    private final OrderService orderService;

    @Operation(summary = "Listado de pedidos con filtro por estado y búsqueda")
    @GetMapping("/orders")
    public PageResponse<OrderResponse> orders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return orderService.adminList(status, q, page, size);
    }

    @Operation(summary = "Cambiar estado de un pedido")
    @PatchMapping("/orders/{orderId}/status")
    public OrderResponse updateStatus(
            @PathVariable Long orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        return orderService.updateStatus(orderId, request.status());
    }
}
