package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ProductHomeResponse {
    private Integer id;
    private String name;
    private BigDecimal basePrice;
    private BigDecimal originalPrice;
    private String imageUrl;           // ảnh chính
}