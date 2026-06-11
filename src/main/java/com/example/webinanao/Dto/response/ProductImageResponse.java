package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductImageResponse {
    private Integer id;
    private String imageUrl;
    private String imageType;
    private String color;
    private Integer sortOrder;
    private Boolean isDefault;
}
