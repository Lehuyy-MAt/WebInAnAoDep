package com.example.webinanao.Dto.request;



import lombok.Data;

@Data
public class CategoryRequest {

    private String name;
    private String description;
    private String imageUrl;
    private Integer sortOrder;
    private Boolean isActive;
}