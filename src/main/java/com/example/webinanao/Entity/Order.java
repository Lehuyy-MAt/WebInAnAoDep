package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "DiscountCodeId")
    private DiscountCode discountCode;

    @Column(name = "OrderNumber")
    private String orderNumber;

    @Column(name = "ReceiverName")
    private String receiverName;

    @Column(name = "ReceiverPhone")
    private String receiverPhone;

    @Column(name = "ShippingAddress")
    private String shippingAddress;

    @Column(name = "City")
    private String city;

    @Column(name = "Subtotal")
    private BigDecimal subtotal;

    @Column(name = "ShippingFee")
    private BigDecimal shippingFee;

    @Column(name = "DiscountAmount")
    private BigDecimal discountAmount;

    @Column(name = "TotalAmount")
    private BigDecimal totalAmount;

    @Column(name = "Status")
    private String status;

    @Column(name = "PaymentMethod")
    private String paymentMethod;

    @Column(name = "Note")
    private String note;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "ConfirmedAt")
    private LocalDateTime confirmedAt;

    @Column(name = "DeliveredAt")
    private LocalDateTime deliveredAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> orderItems = new ArrayList<>();

    // Helper method để thêm item
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    // Helper method để xóa item
    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }

    // Setter đặc biệt để đảm bảo mối quan hệ 2 chiều
    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        if (orderItems != null) {
            for (OrderItem item : orderItems) {
                item.setOrder(this);
            }
        }
    }
}