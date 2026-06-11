package com.example.webinanao.Dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProductImageRequest {

    @NotBlank(message = "URL ảnh không được để trống")
    private String imageUrl;

    private String imageType = "Front"; // Front, Back, Detail, Mockup

    private String color;

    private Integer sortOrder = 0;

    private Boolean isDefault = false;
}
