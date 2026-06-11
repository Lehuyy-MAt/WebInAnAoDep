package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class ProductDetailResponse {
    private Integer id;
    private Integer categoryId;
    private String categoryName;
    private String name;
    private String description;
    private String material;
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private String availableSizes;
    private String availableColors;
    private Integer stockQuantity;
    private Boolean isActive;
    private List<ProductImageResponse> images;
    private Double averageRating;
    private Integer reviewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
