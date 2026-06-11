package com.example.webinanao.Dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductRequest {

    @NotNull(message = "CategoryId không được để trống")
    private Integer categoryId;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm tối đa 255 ký tự")
    private String name;

    @Size(max = 1000, message = "Mô tả tối đa 1000 ký tự")
    private String description;

    private String material;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    private BigDecimal basePrice;

    private BigDecimal originalPrice;

    private String availableSizes;   // e.g. "S,M,L,XL,XXL"
    private String availableColors;  // e.g. "White,Black,Gray,Navy"

    @Min(value = 0, message = "Số lượng tồn kho không được âm")
    private Integer stockQuantity = 0;
}
