package com.example.webinanao.Service;

import com.example.webinanao.Dto.response.CategoryResponse;
import com.example.webinanao.Dto.response.HomeResponse;
import com.example.webinanao.Dto.response.ProductHomeResponse;
import com.example.webinanao.Entity.Category;
import com.example.webinanao.Entity.Product;
import com.example.webinanao.Repo.CategoryRepository;
import com.example.webinanao.Repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;

    public HomeResponse getHomeData() {
        HomeResponse response = new HomeResponse();

        // Danh mục
        response.setCategories(categoryRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream().map(this::toCategoryResponse).collect(Collectors.toList()));

        // Sản phẩm nổi bật
        response.setFeaturedProducts(productRepository.findTop8ByIsActiveTrueOrderByCreatedAtDesc()
                .stream().map(this::toProductHomeResponse).collect(Collectors.toList()));

        // Sản phẩm mới
        response.setNewProducts(productRepository.findTop8ByIsActiveTrueOrderByIdDesc()
                .stream().map(this::toProductHomeResponse).collect(Collectors.toList()));

        return response;
    }

    private CategoryResponse toCategoryResponse(Category category) {
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        dto.setSlug(category.getSlug());
        dto.setImageUrl(category.getImageUrl());
        return dto;
    }

    private ProductHomeResponse toProductHomeResponse(Product product) {
        ProductHomeResponse dto = new ProductHomeResponse();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setBasePrice(product.getBasePrice());
        dto.setOriginalPrice(product.getOriginalPrice());

        // Lấy ảnh mặc định (nếu có)
        if (product.getProductImages() != null && !product.getProductImages().isEmpty()) {
            product.getProductImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getIsDefault()))
                    .findFirst()
                    .ifPresentOrElse(
                            img -> dto.setImageUrl(img.getImageUrl()),
                            () -> dto.setImageUrl(product.getProductImages().get(0).getImageUrl()) // lấy ảnh đầu tiên nếu không có default
                    );
        }

        return dto;
    }
}