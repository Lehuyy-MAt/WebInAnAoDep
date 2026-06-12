package com.example.webinanao.Service;

import com.example.webinanao.Dto.request.CartItemRequest;
import com.example.webinanao.Dto.response.CartItemResponse;
import com.example.webinanao.Dto.response.CartResponse;
import com.example.webinanao.Entity.*;
import com.example.webinanao.Repo.*;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DesignRepository designRepository;

    public CartResponse getCartByUserId(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        return toCartResponse(cart);
    }

    @Transactional
    public CartResponse addToCart(Integer userId, CartItemRequest request) {
        Cart cart = getOrCreateCart(userId);
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));

        // Kiểm tra item đã tồn tại (cùng product + design + size + color)
        CartItem existing = cart.getCartItems().stream()
                .filter(item ->
                        item.getProduct().getId().equals(request.getProductId()) &&
                                item.getSize().equals(request.getSize()) &&
                                item.getColor().equals(request.getColor()) &&
                                (request.getDesignId() == null
                                        ? item.getDesign() == null
                                        : request.getDesignId().equals(item.getDesign() != null ? item.getDesign().getId() : null))
                )
                .findFirst()
                .orElse(null);

        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + request.getQuantity());
            cartItemRepository.save(existing);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setProduct(product);
            newItem.setSize(request.getSize());
            newItem.setColor(request.getColor());
            newItem.setQuantity(request.getQuantity());
            newItem.setUnitPrice(product.getBasePrice());

            if (request.getDesignId() != null) {
                Design design = designRepository.findById(request.getDesignId())
                        .orElseThrow(() -> new ResourceNotFoundException("Thiết kế không tồn tại"));
                newItem.setDesign(design);
            }

            cart.getCartItems().add(newItem);
            cartItemRepository.save(newItem);
        }

        return toCartResponse(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Transactional
    public CartResponse updateCartItem(Integer userId, Integer itemId, Integer quantity) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm trong giỏ không tồn tại"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Sản phẩm không thuộc giỏ hàng của bạn");
        }

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return toCartResponse(cartRepository.findById(cart.getId()).orElse(cart));
    }

    @Transactional
    public void removeCartItem(Integer userId, Integer itemId) {
        Cart cart = getOrCreateCart(userId);
        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm trong giỏ không tồn tại"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new ResourceNotFoundException("Sản phẩm không thuộc giỏ hàng của bạn");
        }

        cartItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(Integer userId) {
        Cart cart = getOrCreateCart(userId);
        cart.getCartItems().clear();
        cartRepository.save(cart);
    }

    // ────────────────────────── helpers ──────────────────────────

    private Cart getOrCreateCart(Integer userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
            Cart cart = new Cart();
            cart.setUser(user);
            return cartRepository.save(cart);
        });
    }

    private CartResponse toCartResponse(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setUserId(cart.getUser().getId());
        response.setUpdatedAt(cart.getUpdatedAt());

        List<CartItemResponse> itemResponses = cart.getCartItems().stream()
                .map(this::toCartItemResponse)
                .collect(Collectors.toList());
        response.setItems(itemResponses);
        response.setTotalItems(itemResponses.stream().mapToInt(CartItemResponse::getQuantity).sum());
        response.setTotalAmount(
                itemResponses.stream()
                        .map(CartItemResponse::getSubtotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        return response;
    }

    private CartItemResponse toCartItemResponse(CartItem item) {
        CartItemResponse dto = new CartItemResponse();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProduct().getName());
        dto.setSize(item.getSize());
        dto.setColor(item.getColor());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));

        // Ảnh sản phẩm
        if (item.getProduct().getProductImages() != null && !item.getProduct().getProductImages().isEmpty()) {
            item.getProduct().getProductImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsDefault()))
                    .findFirst()
                    .ifPresentOrElse(
                            img -> dto.setProductImageUrl(img.getImageUrl()),
                            () -> dto.setProductImageUrl(item.getProduct().getProductImages().get(0).getImageUrl())
                    );
        }

        if (item.getDesign() != null) {
            dto.setDesignId(item.getDesign().getId());
            dto.setDesignName(item.getDesign().getDesignName());
        }

        return dto;
    }
}
