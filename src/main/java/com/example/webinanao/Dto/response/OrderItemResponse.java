package com.example.webinanao.Dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class OrderItemResponse {
    private Integer id;
    private Integer productId;
    private String productName;
    private String productImageUrl;  // 👈 THÊM DÒNG NÀY
    private String size;
    private String color;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal subtotal;
    private String printFileUrl;
    private String note;
    private Integer designId;
    private String designName;
}