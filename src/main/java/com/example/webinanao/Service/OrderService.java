package com.example.webinanao.Service;

import com.example.webinanao.Dto.request.OrderRequest;
import com.example.webinanao.Dto.response.OrderItemResponse;
import com.example.webinanao.Dto.response.OrderResponse;
import com.example.webinanao.Entity.*;
import com.example.webinanao.Repo.*;
import com.example.webinanao.exception.BadRequestException;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderHistoryRepository orderHistoryRepository;  // 👈 THÊM DÒNG NÀY
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final DesignRepository designRepository;
    private final DiscountCodeRepository discountCodeRepository;

    // 👉 PHƯƠNG THỨC LƯU LỊCH SỬ
    private void saveOrderHistory(Order order, String status, String note, String updatedBy) {
        OrderHistory history = new OrderHistory();
        history.setOrder(order);
        history.setStatus(status);
        history.setNote(note);
        history.setUpdatedBy(updatedBy != null ? updatedBy : "system");
        orderHistoryRepository.save(history);
        System.out.println("✅ OrderHistory saved: " + status + " - " + note);
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        System.out.println("=== CREATE ORDER START ===");
        System.out.println("User ID: " + request.getUserId());
        System.out.println("Items count: " + request.getItems().size());

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));

        // 1. Tạo và lưu ORDER trước
        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        order.setShippingAddress(request.getShippingAddress());
        order.setCity(request.getCity());
        order.setPaymentMethod(request.getPaymentMethod());
        order.setNote(request.getNote());
        
        BigDecimal shippingFee = BigDecimal.valueOf(20000);
        if ("Nhận tại cửa hàng".equalsIgnoreCase(request.getShippingAddress())) {
            shippingFee = BigDecimal.ZERO;
        } else if (isHanoi(request.getCity())) {
            shippingFee = BigDecimal.ZERO;
        }
        order.setShippingFee(shippingFee);
        
        order.setStatus("pending");
        order.setCreatedAt(LocalDateTime.now());
        order.setSubtotal(BigDecimal.ZERO);
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setTotalAmount(BigDecimal.ZERO);

        // Lưu order để có ID
        Order savedOrder = orderRepository.save(order);
        System.out.println("✅ Order saved with ID: " + savedOrder.getId());

        // 👉 LƯU LỊCH SỬ TẠO ĐƠN
        saveOrderHistory(savedOrder, "pending", "Tạo đơn hàng mới", user.getFullName());

        // 2. Tạo và lưu từng ORDER_ITEM
        BigDecimal subtotal = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderRequest.OrderItemRequest itemReq : request.getItems()) {
            System.out.println("Processing product ID: " + itemReq.getProductId());

            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại: " + itemReq.getProductId()));

            OrderItem item = new OrderItem();
            item.setOrder(savedOrder);
            item.setProduct(product);
            item.setProductName(product.getName());
            item.setSize(itemReq.getSize());
            item.setColor(itemReq.getColor());
            item.setQuantity(itemReq.getQuantity());
            item.setUnitPrice(itemReq.getUnitPrice());
            item.setSubtotal(itemReq.getUnitPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity())));
            item.setNote(itemReq.getNote());

            // Lưu từng item
            OrderItem savedItem = orderItemRepository.save(item);
            orderItems.add(savedItem);
            subtotal = subtotal.add(item.getSubtotal());

            System.out.println("✅ OrderItem saved with ID: " + savedItem.getId() + " - Product: " + product.getName());
        }

        // 3. Cập nhật order với tổng tiền
        savedOrder.setSubtotal(subtotal);
        savedOrder.setTotalAmount(subtotal.add(savedOrder.getShippingFee()));
        savedOrder.setOrderItems(orderItems);

        Order finalOrder = orderRepository.save(savedOrder);
        System.out.println("✅ Final order updated - Total items: " + finalOrder.getOrderItems().size());
        System.out.println("=== CREATE ORDER END ===");

        return toOrderResponse(finalOrder);
    }

    public List<OrderResponse> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với id: " + orderId));
        return toOrderResponse(order);
    }

    @Transactional
    public OrderResponse cancelOrder(Integer orderId, String cancelledBy, String reason) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với id: " + orderId));

        if (!"pending".equalsIgnoreCase(order.getStatus())) {
            throw new BadRequestException("Chỉ có thể huỷ đơn hàng ở trạng thái pending");
        }

        order.setStatus("cancelled");
        Order cancelledOrder = orderRepository.save(order);

        // 👉 LƯU LỊCH SỬ HỦY ĐƠN
        String cancelReason = reason != null ? "Lý do hủy: " + reason : "Khách hàng yêu cầu hủy đơn";
        saveOrderHistory(cancelledOrder, "cancelled", cancelReason, cancelledBy != null ? cancelledBy : userRepository.findById(order.getUser().getId()).map(User::getFullName).orElse("user"));

        return toOrderResponse(cancelledOrder);
    }

    @Transactional
    public OrderResponse updateOrderStatus(Integer orderId, String status, String updatedBy, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại với id: " + orderId));

        String oldStatus = order.getStatus();
        order.setStatus(status.toLowerCase());

        if ("confirmed".equalsIgnoreCase(status)) {
            order.setConfirmedAt(LocalDateTime.now());
        } else if ("delivered".equalsIgnoreCase(status)) {
            order.setDeliveredAt(LocalDateTime.now());
        }

        Order updatedOrder = orderRepository.save(order);

        // 👉 LƯU LỊCH SỬ THAY ĐỔI
        String historyNote = note != null ? note : "Thay đổi trạng thái từ " + oldStatus + " sang " + status;
        saveOrderHistory(updatedOrder, status, historyNote, updatedBy != null ? updatedBy : "admin");

        return toOrderResponse(updatedOrder);
    }

    // 👉 LẤY LỊCH SỬ ĐƠN HÀNG
    public List<OrderHistory> getOrderHistory(Integer orderId) {
        return orderHistoryRepository.findByOrderIdOrderByCreatedAtDesc(orderId);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toOrderResponse)
                .collect(Collectors.toList());
    }

    public boolean isOrderPaid(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return !"pending".equalsIgnoreCase(order.getStatus())
                && !"cancelled".equalsIgnoreCase(order.getStatus());
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse dto = new OrderResponse();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setUserId(order.getUser().getId());
        dto.setUserFullName(order.getUser().getFullName());
        dto.setReceiverName(order.getReceiverName());
        dto.setReceiverPhone(order.getReceiverPhone());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setCity(order.getCity());
        dto.setSubtotal(order.getSubtotal());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setPaymentMethod(order.getPaymentMethod());
        dto.setNote(order.getNote());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setConfirmedAt(order.getConfirmedAt());
        dto.setDeliveredAt(order.getDeliveredAt());

        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(this::toOrderItemResponse)
                .collect(Collectors.toList());
        dto.setItems(itemResponses);

        return dto;
    }

    private OrderItemResponse toOrderItemResponse(OrderItem item) {
        OrderItemResponse dto = new OrderItemResponse();
        dto.setId(item.getId());
        dto.setProductId(item.getProduct().getId());
        dto.setProductName(item.getProductName());

        String imageUrl = null;
        if (item.getProduct() != null) {
            try {
                if (item.getProduct().getProductImages() != null && !item.getProduct().getProductImages().isEmpty()) {
                    for (ProductImage img : item.getProduct().getProductImages()) {
                        if (img.getIsDefault() != null && img.getIsDefault()) {
                            imageUrl = img.getImageUrl();
                            break;
                        }
                    }
                    if (imageUrl == null) {
                        imageUrl = item.getProduct().getProductImages().get(0).getImageUrl();
                    }
                }
                System.out.println("Product ID: " + item.getProduct().getId() + " - Image URL: " + imageUrl);
            } catch (Exception e) {
                System.out.println("Error getting image: " + e.getMessage());
            }
        }
        dto.setProductImageUrl(imageUrl);

        dto.setSize(item.getSize());
        dto.setColor(item.getColor());
        dto.setQuantity(item.getQuantity());
        dto.setUnitPrice(item.getUnitPrice());
        dto.setSubtotal(item.getSubtotal());
        dto.setPrintFileUrl(item.getPrintFileUrl());
        dto.setNote(item.getNote());

        if (item.getDesign() != null) {
            dto.setDesignId(item.getDesign().getId());
            dto.setDesignName(item.getDesign().getDesignName());
        }

        return dto;
    }

    private boolean isHanoi(String city) {
        if (city == null) {
            return false;
        }
        String normalized = java.text.Normalizer.normalize(city, java.text.Normalizer.Form.NFD);
        String noAccents = normalized.replaceAll("\\p{M}", "");
        String cleaned = noAccents.toLowerCase()
                .replace("đ", "d")
                .replace("Đ", "d")
                .trim();
        return cleaned.contains("ha noi") || cleaned.contains("hanoi");
    }
}