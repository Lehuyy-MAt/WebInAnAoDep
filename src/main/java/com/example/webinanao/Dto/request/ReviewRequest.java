package com.example.webinanao.Dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ReviewRequest {

    @NotNull(message = "UserId không được để trống")
    private Integer userId;

    @NotNull(message = "ProductId không được để trống")
    private Integer productId;

    @NotNull(message = "OrderId không được để trống")
    private Integer orderId;

    @NotNull(message = "Rating không được để trống")
    @Min(value = 1, message = "Rating tối thiểu là 1")
    @Max(value = 5, message = "Rating tối đa là 5")
    private Byte rating;

    private String title;

    private String comment;

    private String imageUrls; // JSON array string, e.g. ["url1","url2"]
}
