package com.uiwave.ecommerce.order;

import com.uiwave.ecommerce.cart.model.Cart;
import com.uiwave.ecommerce.cart.model.CartItem;
import com.uiwave.ecommerce.cart.repository.CartRepository;
import com.uiwave.ecommerce.catalog.model.Product;
import com.uiwave.ecommerce.catalog.repository.ProductRepository;
import com.uiwave.ecommerce.common.dto.PageResponse;
import com.uiwave.ecommerce.common.exception.BadRequestException;
import com.uiwave.ecommerce.common.exception.ResourceNotFoundException;
import com.uiwave.ecommerce.order.dto.CheckoutRequest;
import com.uiwave.ecommerce.order.dto.DailySalesResponse;
import com.uiwave.ecommerce.order.dto.LowStockProductResponse;
import com.uiwave.ecommerce.order.dto.OrderResponse;
import com.uiwave.ecommerce.order.dto.OrderStatsResponse;
import com.uiwave.ecommerce.order.model.OrderStatus;
import com.uiwave.ecommerce.order.model.ShopOrder;
import com.uiwave.ecommerce.order.model.ShopOrderItem;
import com.uiwave.ecommerce.order.repository.ShopOrderRepository;
import com.uiwave.ecommerce.user.model.User;
import com.uiwave.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final ShopOrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    /**
     * Checkout: valida stock con bloqueo pesimista, crea el pedido con los precios
     * actuales, descuenta inventario y vacía el carrito. Todo en una transacción.
     */
    @Transactional
    public OrderResponse checkout(String email, CheckoutRequest request) {
        User user = findUser(email);
        Cart cart = cartRepository.findByUserId(user.getId())
                .orElseThrow(() -> new BadRequestException("Tu carrito está vacío"));

        if (cart.getItems().isEmpty()) {
            throw new BadRequestException("Tu carrito está vacío");
        }

        ShopOrder order = ShopOrder.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .fullName(request.fullName().trim())
                .phone(request.phone().trim())
                .address(request.address().trim())
                .city(request.city().trim())
                .postalCode(request.postalCode().trim())
                .country(request.country().trim())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                    .filter(Product::isActive)
                    .orElseThrow(() -> new BadRequestException(
                            "El producto " + cartItem.getProduct().getName() + " ya no está disponible"));

            if (product.getStock() < cartItem.getQuantity()) {
                throw new BadRequestException("Stock insuficiente para " + product.getName()
                        + " (disponible: " + product.getStock() + ")");
            }

            product.setStock(product.getStock() - cartItem.getQuantity());
            BigDecimal unitPrice = product.getPrice();

            order.getItems().add(ShopOrderItem.builder()
                    .order(order)
                    .product(product)
                    .productName(product.getName())
                    .unitPrice(unitPrice)
                    .quantity(cartItem.getQuantity())
                    .build());

            total = total.add(unitPrice.multiply(BigDecimal.valueOf(cartItem.getQuantity())));
        }

        order.setTotal(total.setScale(2, RoundingMode.HALF_UP));
        ShopOrder saved = orderRepository.save(order);
        cart.getItems().clear();

        return OrderResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> listMine(String email, int page, int size) {
        User user = findUser(email);
        Page<ShopOrder> result = orderRepository.findByUserId(user.getId(), pageable(page, size));
        return PageResponse.from(result, OrderResponse::from);
    }

    @Transactional(readOnly = true)
    public OrderResponse getMine(String email, Long orderId) {
        User user = findUser(email);
        ShopOrder order = orderRepository.findByIdAndUserId(orderId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));
        return OrderResponse.from(order);
    }

    // -------------------------------------------------------------------- admin

    @Transactional(readOnly = true)
    public PageResponse<OrderResponse> adminList(
            OrderStatus status, String query, int page, int size) {
        String cleanQuery = query != null && !query.isBlank() ? query.trim() : null;
        Page<ShopOrder> result = orderRepository.searchAdmin(cleanQuery, status, pageable(page, size));
        return PageResponse.from(result, OrderResponse::from);
    }

    @Transactional
    public OrderResponse updateStatus(Long orderId, OrderStatus newStatus) {
        ShopOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        OrderStatus current = order.getStatus();
        if (!isTransitionAllowed(current, newStatus)) {
            throw new BadRequestException(
                    "Transición de estado no permitida: " + current + " → " + newStatus);
        }

        if (newStatus == OrderStatus.CANCELLED) {
            restoreStock(order);
        }

        order.setStatus(newStatus);
        return OrderResponse.from(order);
    }

    @Transactional(readOnly = true)
    public OrderStatsResponse stats() {
        LocalDate today = LocalDate.now();
        Instant since = today.minusDays(6)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        List<ShopOrder> recent = orderRepository
                .findByStatusNotAndCreatedAtAfter(OrderStatus.CANCELLED, since);

        Map<LocalDate, List<ShopOrder>> byDay = recent.stream()
                .collect(Collectors.groupingBy(order -> order.getCreatedAt()
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()));

        List<DailySalesResponse> salesByDay = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate day = today.minusDays(i);
            List<ShopOrder> dayOrders = byDay.getOrDefault(day, List.of());
            BigDecimal revenue = dayOrders.stream()
                    .map(ShopOrder::getTotal)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            salesByDay.add(new DailySalesResponse(day, dayOrders.size(), revenue));
        }

        List<LowStockProductResponse> lowStock = productRepository
                .findTop5ByActiveTrueAndStockLessThanEqualOrderByStockAsc(5)
                .stream()
                .map(product -> new LowStockProductResponse(
                        product.getId(), product.getName(), product.getStock()))
                .toList();

        return new OrderStatsResponse(
                userRepository.count(),
                productRepository.countByActiveTrue(),
                orderRepository.count(),
                orderRepository.countByStatus(OrderStatus.PENDING),
                orderRepository.sumTotalExcluding(OrderStatus.CANCELLED),
                salesByDay,
                lowStock);
    }

    // ----------------------------------------------------------------- helpers

    private void restoreStock(ShopOrder order) {
        for (ShopOrderItem item : order.getItems()) {
            productRepository.findByIdForUpdate(item.getProduct().getId())
                    .ifPresent(product -> product.setStock(product.getStock() + item.getQuantity()));
        }
    }

    private static boolean isTransitionAllowed(OrderStatus from, OrderStatus to) {
        return switch (from) {
            case PENDING -> to == OrderStatus.PAID || to == OrderStatus.CANCELLED;
            case PAID -> to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
            case SHIPPED -> to == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }

    private User findUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    private static Pageable pageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
