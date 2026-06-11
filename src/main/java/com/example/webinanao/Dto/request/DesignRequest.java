package com.example.webinanao.Dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DesignRequest {

    @NotNull(message = "UserId không được để trống")
    private Integer userId;

    @NotNull(message = "ProductId không được để trống")
    private Integer productId;

    @NotBlank(message = "Tên thiết kế không được để trống")
    private String designName;

    private String previewUrl;

    private String designData; // JSON string

    private String size;
    private String color;

    private Boolean isPublic = false;
}