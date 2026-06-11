package com.example.webinanao.Service;
import com.example.webinanao.Dto.request.CategoryRequest;
import com.example.webinanao.Dto.response.CategoryResponse;
import com.example.webinanao.Entity.Category;
import com.example.webinanao.Repo.CategoryRepository;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findByIsActiveTrueOrderBySortOrderAsc()
                .stream()
                .map(this::toCategoryResponse)
                .collect(Collectors.toList());
    }

    public CategoryResponse getCategoryById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại với id: " + id));
        return toCategoryResponse(category);
    }

    public CategoryResponse getCategoryBySlug(String slug) {
        Category category = categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại với slug: " + slug));
        return toCategoryResponse(category);
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
    public CategoryResponse createCategory(CategoryRequest request) {

        Category category = new Category();

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());
        category.setIsActive(
                request.getIsActive() != null
                        ? request.getIsActive()
                        : true
        );

        String slug = request.getName()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-");

        category.setSlug(slug);

        Category saved = categoryRepository.save(category);

        return toCategoryResponse(saved);
    }
    public CategoryResponse updateCategory(Integer id,
                                           CategoryRequest request) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Danh mục không tồn tại với id: " + id));

        category.setName(request.getName());
        category.setDescription(request.getDescription());
        category.setImageUrl(request.getImageUrl());
        category.setSortOrder(request.getSortOrder());
        category.setIsActive(request.getIsActive());

        String slug = request.getName()
                .trim()
                .toLowerCase()
                .replaceAll("\\s+", "-");

        category.setSlug(slug);

        Category updated = categoryRepository.save(category);

        return toCategoryResponse(updated);
    }
    public void deleteCategory(Integer id) {

        Category category = categoryRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Danh mục không tồn tại với id: " + id));

        categoryRepository.delete(category);
    }
}
