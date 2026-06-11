package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CartItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImageUrl;
    private Integer designId;
    private String designName;
    private String size;
    private String color;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
}
