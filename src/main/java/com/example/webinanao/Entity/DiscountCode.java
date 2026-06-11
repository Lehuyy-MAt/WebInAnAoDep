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
@Table(name = "DiscountCodes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @Column(name = "Code", unique = true)
    private String code;

    @Column(name = "DiscountType")
    private String discountType;

    @Column(name = "DiscountValue")
    private BigDecimal discountValue;

    @Column(name = "MinOrderAmount")
    private BigDecimal minOrderAmount;

    @Column(name = "MaxDiscount")
    private BigDecimal maxDiscount;

    @Column(name = "UsageLimit")
    private Integer usageLimit;

    @Column(name = "UsedCount")
    private Integer usedCount;

    @Column(name = "StartDate")
    private LocalDateTime startDate;

    @Column(name = "EndDate")
    private LocalDateTime endDate;

    @Column(name = "IsActive")
    private Boolean isActive;

    @OneToMany(mappedBy = "discountCode")
    private List<Order> orders = new ArrayList<>();
}