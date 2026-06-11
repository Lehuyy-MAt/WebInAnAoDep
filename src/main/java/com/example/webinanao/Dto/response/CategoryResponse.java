package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryResponse {
    private Integer id;
    private String name;
    private String description;
    private String slug;
    private String imageUrl;
}