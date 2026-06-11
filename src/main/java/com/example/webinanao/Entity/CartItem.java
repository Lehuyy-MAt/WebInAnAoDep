package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "CartItems")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "CartId")
    private Cart cart;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "DesignId")
    private Design design;

    @Column(name = "Size")
    private String size;

    @Column(name = "Color")
    private String color;

    @Column(name = "Quantity")
    private Integer quantity;

    @Column(name = "UnitPrice")
    private BigDecimal unitPrice;

    @Column(name = "AddedAt")
    private LocalDateTime addedAt;
}