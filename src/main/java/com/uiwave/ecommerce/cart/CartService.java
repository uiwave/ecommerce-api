package com.uiwave.ecommerce.cart;

import com.uiwave.ecommerce.cart.dto.AddCartItemRequest;
import com.uiwave.ecommerce.cart.dto.CartItemResponse;
import com.uiwave.ecommerce.cart.dto.CartResponse;
import com.uiwave.ecommerce.cart.dto.UpdateCartItemRequest;
import com.uiwave.ecommerce.cart.model.Cart;
import com.uiwave.ecommerce.cart.model.CartItem;
import com.uiwave.ecommerce.cart.repository.CartItemRepository;
import com.uiwave.ecommerce.cart.repository.CartRepository;
import com.uiwave.ecommerce.catalog.model.Product;
import com.uiwave.ecommerce.catalog.repository.ProductRepository;
import com.uiwave.ecommerce.common.exception.BadRequestException;
import com.uiwave.ecommerce.common.exception.ResourceNotFoundException;
import com.uiwave.ecommerce.user.model.User;
import com.uiwave.ecommerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public CartResponse getCart(String email) {
        return toResponse(getOrCreateCart(email));
    }

    @Transactional
    public CartResponse addItem(String email, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        Product product = productRepository.findById(request.productId())
                .filter(Product::isActive)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        CartItem item = cartItemRepository
                .findByCartIdAndProductId(cart.getId(), product.getId())
                .orElse(null);

        int newQuantity = request.quantity() + (item != null ? item.getQuantity() : 0);
        validateStock(product, newQuantity);

        if (item == null) {
            item = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(request.quantity())
                    .build();
            cartItemRepository.save(item);
            cart.getItems().add(item);
        } else {
            item.setQuantity(newQuantity);
        }

        return toResponse(cart);
    }

    @Transactional
    public CartResponse updateItem(String email, Long itemId, UpdateCartItemRequest request) {
        Cart cart = getOrCreateCart(email);
        CartItem item = findItem(cart, itemId);

        validateStock(item.getProduct(), request.quantity());
        item.setQuantity(request.quantity());

        return toResponse(cart);
    }

    @Transactional
    public CartResponse removeItem(String email, Long itemId) {
        Cart cart = getOrCreateCart(email);
        CartItem item = findItem(cart, itemId);
        cart.getItems().remove(item);

        return toResponse(cart);
    }

    @Transactional
    public void clear(String email) {
        Cart cart = getOrCreateCart(email);
        cart.getItems().clear();
    }

    // ----------------------------------------------------------------- helpers

    private Cart getOrCreateCart(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        return cartRepository.findByUserId(user.getId())
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    private CartItem findItem(Cart cart, Long itemId) {
        return cartItemRepository.findByIdAndCartId(itemId, cart.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Ítem del carrito no encontrado"));
    }

    private static void validateStock(Product product, int quantity) {
        if (quantity > product.getStock()) {
            throw new BadRequestException(
                    "Stock insuficiente para " + product.getName()
                            + " (disponible: " + product.getStock() + ")");
        }
    }

    private static CartResponse toResponse(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
                .map(CartItemResponse::from)
                .toList();

        int totalItems = items.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal total = items.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        return new CartResponse(items, totalItems, total);
    }
}
