package com.example.webinanao.Dto.response;


import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter @Setter
public class ProductCreateRequest {
    private Integer categoryId;
    private String name;
    private String description;
    private String material;
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private String availableSizes;
    private String availableColors;
}