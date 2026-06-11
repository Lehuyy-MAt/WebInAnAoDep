package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class ProductListResponse {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private Integer stockQuantity;
    private Boolean isActive;
    private String defaultImageUrl;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
}
