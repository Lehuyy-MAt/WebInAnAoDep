package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "OrderItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "OrderId", nullable = false)  // 👈 THÊM nullable = false
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "DesignId")
    private Design design;

    @Column(name = "ProductName")
    private String productName;

    @Column(name = "Size")
    private String size;

    @Column(name = "Color")
    private String color;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;

    @Column(name = "Subtotal")
    private BigDecimal subtotal;

    @Column(name = "PrintFileUrl")
    private String printFileUrl;

    @Column(name = "Note")
    private String note;
}