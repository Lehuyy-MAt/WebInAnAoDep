package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.CartItemRequest;
import com.example.webinanao.Dto.response.CartResponse;
import com.example.webinanao.Service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // Xem giỏ hàng của user
    @GetMapping("/{userId}")
    public ResponseEntity<CartResponse> getCart(@PathVariable Integer userId) {
        return ResponseEntity.ok(cartService.getCartByUserId(userId));
    }

    // Thêm sản phẩm vào giỏ
    @PostMapping("/{userId}/add")
    public ResponseEntity<CartResponse> addToCart(
            @PathVariable Integer userId,
            @Valid @RequestBody CartItemRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request));
    }

    // Cập nhật số lượng sản phẩm trong giỏ
    @PutMapping("/{userId}/items/{itemId}")
    public ResponseEntity<CartResponse> updateCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(cartService.updateCartItem(userId, itemId, quantity));
    }

    // Xóa sản phẩm khỏi giỏ
    @DeleteMapping("/{userId}/items/{itemId}")
    public ResponseEntity<Void> removeCartItem(
            @PathVariable Integer userId,
            @PathVariable Integer itemId) {
        cartService.removeCartItem(userId, itemId);
        return ResponseEntity.noContent().build();
    }

    // Xóa toàn bộ giỏ hàng
    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}