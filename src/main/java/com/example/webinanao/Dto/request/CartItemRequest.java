package com.example.webinanao.Dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotNull(message = "ProductId không được để trống")
    private Integer productId;

    private Integer designId; // optional

    @NotBlank(message = "Size không được để trống")
    private String size;

    @NotBlank(message = "Color không được để trống")
    private String color;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 1, message = "Số lượng phải ít nhất là 1")
    private Integer quantity;
}
