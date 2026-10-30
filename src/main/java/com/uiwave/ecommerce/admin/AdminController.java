package com.uiwave.ecommerce.admin;

import com.uiwave.ecommerce.auth.dto.UserResponse;
import com.uiwave.ecommerce.common.dto.PageResponse;
import com.uiwave.ecommerce.order.dto.OrderStatsResponse;
import com.uiwave.ecommerce.order.OrderService;
import com.uiwave.ecommerce.user.dto.UpdateUserRequest;
import com.uiwave.ecommerce.user.model.Role;
import com.uiwave.ecommerce.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
@Tag(name = "Administración", description = "Endpoints exclusivos para administradores")
public class AdminController {

    private final UserService userService;
    private final OrderService orderService;

    @Operation(summary = "Endpoint de prueba: solo accesible con rol ADMIN")
    @GetMapping("/hello")
    public Map<String, Object> hello(Authentication authentication) {
        return Map.of(
                "message", "Hola, administrador",
                "username", authentication.getName(),
                "timestamp", Instant.now());
    }

    @Operation(summary = "Listado paginado de usuarios con búsqueda y filtro por rol")
    @GetMapping("/users")
    public PageResponse<UserResponse> users(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return userService.listUsers(q, role, page, size);
    }

    @Operation(summary = "Cambiar rol o estado de una cuenta")
    @PatchMapping("/users/{id}")
    public UserResponse updateUser(
            @PathVariable Long id,
            @Valid @RequestBody UpdateUserRequest request,
            Authentication authentication) {
        return userService.updateUser(id, request, authentication.getName());
    }

    @Operation(summary = "Estadísticas generales (incluye ventas por día y stock bajo)")
    @GetMapping("/stats")
    public OrderStatsResponse stats() {
        return orderService.stats();
    }
}
