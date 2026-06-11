package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class HomeResponse {
    private List<CategoryResponse> categories;
    private List<ProductHomeResponse> featuredProducts;
    private List<ProductHomeResponse> newProducts;
}