package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CartResponse {
    private Integer id;
    private Integer userId;
    private List<CartItemResponse> items;
    private BigDecimal totalAmount;
    private Integer totalItems;
    private LocalDateTime updatedAt;
}
