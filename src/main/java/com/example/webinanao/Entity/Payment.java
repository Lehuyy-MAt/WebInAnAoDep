package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "Payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "OrderId")
    private Order order;

    @Column(name = "TransactionId")
    private String transactionId;

    @Column(name = "Amount")
    private BigDecimal amount;

    @Column(name = "PaymentMethod")
    private String paymentMethod;

    @Column(name = "Status")
    private String status;

    @Column(name = "ResponseData", columnDefinition = "nvarchar(max)")
    private String responseData;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;
}