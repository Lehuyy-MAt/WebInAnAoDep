package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class OrderResponse {
    private Integer id;
    private String orderNumber;
    private Integer userId;
    private String userFullName;
    private String receiverName;
    private String receiverPhone;
    private String shippingAddress;
    private String city;
    private BigDecimal subtotal;
    private BigDecimal shippingFee;
    private BigDecimal discountAmount;
    private BigDecimal totalAmount;
    private String status;
    private String paymentMethod;
    private String note;
    private LocalDateTime createdAt;
    private LocalDateTime confirmedAt;
    private LocalDateTime deliveredAt;
    private List<OrderItemResponse> items;
}
