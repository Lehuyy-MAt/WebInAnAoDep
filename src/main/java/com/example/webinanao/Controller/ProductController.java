package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.ProductImageRequest;
import com.example.webinanao.Dto.request.ProductRequest;
import com.example.webinanao.Dto.response.PageResponse;
import com.example.webinanao.Dto.response.ProductDetailResponse;
import com.example.webinanao.Dto.response.ProductImageResponse;
import com.example.webinanao.Dto.response.ProductListResponse;
import com.example.webinanao.Service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ──────────────────────────────────────────────
    //  PUBLIC
    // ──────────────────────────────────────────────

    @GetMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> getProductDetail(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getProductDetail(id));
    }

    @GetMapping("/search")
    public ResponseEntity<PageResponse<ProductListResponse>> searchProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        return ResponseEntity.ok(
                productService.searchProducts(categoryId, keyword, minPrice, maxPrice, page, size, sortBy, sortDir)
        );
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<ProductListResponse>> getByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.ok(productService.getByCategory(categoryId));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<List<ProductImageResponse>> getImages(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.getImages(id));
    }

    // ──────────────────────────────────────────────
    //  ADMIN
    // ──────────────────────────────────────────────

    @GetMapping("/admin")
    public ResponseEntity<PageResponse<ProductListResponse>> getAllForAdmin(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(productService.getAllForAdmin(keyword, isActive, page, size));
    }

    @PostMapping
    public ResponseEntity<ProductDetailResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDetailResponse> updateProduct(
            @PathVariable Integer id,
            @Valid @RequestBody ProductRequest request
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }

    @PatchMapping("/{id}/toggle-active")
    public ResponseEntity<ProductDetailResponse> toggleActive(@PathVariable Integer id) {
        return ResponseEntity.ok(productService.toggleActive(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    //  ADMIN: quản lý ảnh (URL thủ công)
    // ──────────────────────────────────────────────

    @PostMapping("/{id}/images")
    public ResponseEntity<ProductImageResponse> addImage(
            @PathVariable Integer id,
            @Valid @RequestBody ProductImageRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addImage(id, request));
    }

    @PutMapping("/{id}/images/{imageId}")
    public ResponseEntity<ProductImageResponse> updateImage(
            @PathVariable Integer id,
            @PathVariable Integer imageId,
            @Valid @RequestBody ProductImageRequest request
    ) {
        return ResponseEntity.ok(productService.updateImage(id, imageId, request));
    }

    @DeleteMapping("/{id}/images/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable Integer id,
            @PathVariable Integer imageId
    ) {
        productService.deleteImage(id, imageId);
        return ResponseEntity.noContent().build();
    }

    // ──────────────────────────────────────────────
    //  ADMIN: upload file ảnh (UPLOAD FILE)
    // ──────────────────────────────────────────────

    /**
     * POST /api/products/{id}/upload
     * Admin: upload ảnh từ file
     */
    @PostMapping("/{id}/upload")
    public ResponseEntity<ProductImageResponse> uploadImage(
            @PathVariable Integer id,
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(productService.uploadImage(id, file));
    }
}