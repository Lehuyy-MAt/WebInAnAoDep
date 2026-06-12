package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.OrderRequest;
import com.example.webinanao.Dto.response.OrderResponse;
import com.example.webinanao.Entity.Order;
import com.example.webinanao.Entity.OrderHistory;
import com.example.webinanao.Repo.OrderRepository;
import com.example.webinanao.Service.OrderService;
import com.example.webinanao.exception.ResourceNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderRepository orderRepository;  // 👈 THÊM DÒNG NÀY


    // Đặt hàng mới
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request) {
        return ResponseEntity.ok(orderService.createOrder(request));
    }

    // Lấy danh sách đơn hàng của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(orderService.getOrdersByUserId(userId));
    }

    // Xem chi tiết một đơn hàng
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @PutMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Integer orderId,
            @RequestParam(required = false) String reason,
            @RequestParam(required = false) String cancelledBy) {
        String canceller = cancelledBy != null ? cancelledBy : "user";
        return ResponseEntity.ok(orderService.cancelOrder(orderId, canceller, reason));
    }

    // 👉 SỬA LẠI - Thêm tham số updatedBy và note
    @PutMapping("/{orderId}/status")
    public ResponseEntity<OrderResponse> updateStatus(
            @PathVariable Integer orderId,
            @RequestParam String status,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) String updatedBy) {
        String updater = updatedBy != null ? updatedBy : "admin";
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status, updater, note));
    }


    // Admin: lấy tất cả đơn hàng
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/admin/{orderId}")
    public ResponseEntity<OrderResponse> getOrderDetailForAdmin(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    // 👉 THÊM ENDPOINT KIỂM TRA TRẠNG THÁI THANH TOÁN
    @GetMapping("/{orderId}/payment-status")
    public ResponseEntity<?> checkPaymentStatus(@PathVariable Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

        // Đã thanh toán nếu status không phải pending hoặc cancelled
        boolean isPaid = !"pending".equalsIgnoreCase(order.getStatus())
                && !"cancelled".equalsIgnoreCase(order.getStatus());

        return ResponseEntity.ok(Map.of(
                "paid", isPaid,
                "status", order.getStatus(),
                "orderId", orderId,
                "orderNumber", order.getOrderNumber()
        ));
    }

    @GetMapping("/{orderId}/history")
    public ResponseEntity<List<OrderHistory>> getOrderHistory(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderService.getOrderHistory(orderId));
    }
}