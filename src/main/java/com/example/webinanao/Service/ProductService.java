package com.example.webinanao.Service;

import com.example.webinanao.Dto.request.ProductImageRequest;
import com.example.webinanao.Dto.request.ProductRequest;
import com.example.webinanao.Dto.response.PageResponse;
import com.example.webinanao.Dto.response.ProductDetailResponse;
import com.example.webinanao.Dto.response.ProductImageResponse;
import com.example.webinanao.Dto.response.ProductListResponse;
import com.example.webinanao.Entity.Category;
import com.example.webinanao.Entity.Product;
import com.example.webinanao.Entity.ProductImage;
import com.example.webinanao.Repo.CategoryRepository;
import com.example.webinanao.Repo.ProductImageRepository;
import com.example.webinanao.Repo.ProductRepository;
import com.example.webinanao.exception.BadRequestException;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository     productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository    categoryRepository;
    private final ImageUploadService    imageUploadService;  // ← ĐÃ THÊM

    // ──────────────────────────────────────────────
    //  PUBLIC: xem sản phẩm
    // ──────────────────────────────────────────────

    /** Chi tiết sản phẩm (kèm ảnh + rating) */
    public ProductDetailResponse getProductDetail(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với id: " + id));
        return toDetailResponse(product);
    }

    /** Tìm kiếm / lọc sản phẩm có phân trang */
    public PageResponse<ProductListResponse> searchProducts(
            Integer categoryId,
            String keyword,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            int page,
            int size,
            String sortBy,
            String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();

        Page<Product> productPage = productRepository.searchProducts(categoryId, kw, minPrice, maxPrice, pageable);
        List<ProductListResponse> content = productPage.getContent()
                .stream().map(this::toListResponse).collect(Collectors.toList());

        return PageResponse.from(productPage, content);
    }

    /** Lấy tất cả sản phẩm theo danh mục */
    public List<ProductListResponse> getByCategory(Integer categoryId) {
        return productRepository.findByCategoryIdAndIsActiveTrue(categoryId)
                .stream().map(this::toListResponse).collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────
    //  ADMIN: quản lý sản phẩm
    // ──────────────────────────────────────────────

    /** Admin: danh sách tất cả sản phẩm có phân trang */
    public PageResponse<ProductListResponse> getAllForAdmin(
            String keyword,
            Boolean isActive,
            int page,
            int size
    ) {
        String kw = (keyword == null || keyword.isBlank()) ? null : keyword.trim();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Product> productPage = productRepository.findAllForAdmin(kw, isActive, pageable);
        List<ProductListResponse> content = productPage.getContent()
                .stream().map(this::toListResponse).collect(Collectors.toList());
        return PageResponse.from(productPage, content);
    }

    /** Tạo sản phẩm mới */
    @Transactional
    public ProductDetailResponse createProduct(ProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại với id: " + request.getCategoryId()));

        Product product = new Product();
        mapRequestToEntity(request, product, category);
        return toDetailResponse(productRepository.save(product));
    }

    /** Cập nhật sản phẩm */
    @Transactional
    public ProductDetailResponse updateProduct(Integer id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với id: " + id));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Danh mục không tồn tại với id: " + request.getCategoryId()));

        mapRequestToEntity(request, product, category);
        return toDetailResponse(productRepository.save(product));
    }

    /** Ẩn / hiện sản phẩm (soft delete) */
    @Transactional
    public ProductDetailResponse toggleActive(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với id: " + id));

        boolean current = Boolean.TRUE.equals(product.getIsActive());
        product.setIsActive(!current);
        return toDetailResponse(productRepository.save(product));
    }
    /** Xoá vĩnh viễn sản phẩm */
    @Transactional
    public void deleteProduct(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với id: " + id));
        productRepository.delete(product);
    }

    // ──────────────────────────────────────────────
    //  ADMIN: quản lý ảnh sản phẩm
    // ──────────────────────────────────────────────

    /** Lấy danh sách ảnh */
    public List<ProductImageResponse> getImages(Integer productId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với id: " + productId));
        return productImageRepository.findByProductIdOrderBySortOrderAsc(productId)
                .stream().map(this::toImageResponse).collect(Collectors.toList());
    }

    /** Thêm ảnh mới (URL thủ công) */
    @Transactional
    public ProductImageResponse addImage(Integer productId, ProductImageRequest request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với id: " + productId));

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            productImageRepository.clearDefaultByProductId(productId);
        }

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(request.getImageUrl());
        image.setImageType(request.getImageType() != null ? request.getImageType() : "Front");
        image.setColor(request.getColor());
        image.setSortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0);
        image.setIsDefault(request.getIsDefault() != null ? request.getIsDefault() : false);

        return toImageResponse(productImageRepository.save(image));
    }

    /** Upload ảnh từ file (upload lên Cloudinary) - ĐÃ THÊM */
    @Transactional
    public ProductImageResponse uploadImage(Integer productId, MultipartFile file) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại với id: " + productId));

        String imageUrl = imageUploadService.uploadImage(file);

        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(imageUrl);
        image.setImageType("Upload");
        image.setSortOrder(0);
        image.setIsDefault(false);

        return toImageResponse(productImageRepository.save(image));
    }

    /** Cập nhật ảnh */
    @Transactional
    public ProductImageResponse updateImage(Integer productId, Integer imageId, ProductImageRequest request) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Ảnh không tồn tại với id: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new BadRequestException("Ảnh không thuộc sản phẩm này");
        }

        if (Boolean.TRUE.equals(request.getIsDefault())) {
            productImageRepository.clearDefaultByProductId(productId);
        }

        if (request.getImageUrl() != null) image.setImageUrl(request.getImageUrl());
        if (request.getImageType() != null) image.setImageType(request.getImageType());
        if (request.getColor() != null) image.setColor(request.getColor());
        if (request.getSortOrder() != null) image.setSortOrder(request.getSortOrder());
        if (request.getIsDefault() != null) image.setIsDefault(request.getIsDefault());

        return toImageResponse(productImageRepository.save(image));
    }

    /** Xoá ảnh - ĐÃ SỬA (thêm xóa trên Cloudinary) */
    @Transactional
    public void deleteImage(Integer productId, Integer imageId) {
        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Ảnh không tồn tại với id: " + imageId));

        if (!image.getProduct().getId().equals(productId)) {
            throw new BadRequestException("Ảnh không thuộc sản phẩm này");
        }

        // Xóa trên Cloudinary nếu là ảnh upload
        if (image.getImageUrl() != null && image.getImageUrl().contains("cloudinary")) {
            try {
                String publicId = imageUploadService.getPublicIdFromUrl(image.getImageUrl());
                if (publicId != null) {
                    imageUploadService.deleteImage(publicId);
                }
            } catch (Exception e) {
                // Log error
            }
        }

        productImageRepository.delete(image);
    }

    // ──────────────────────────────────────────────
    //  Helpers
    // ──────────────────────────────────────────────

    private void mapRequestToEntity(ProductRequest request, Product product, Category category) {
        product.setCategory(category);
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setMaterial(request.getMaterial());
        product.setBasePrice(request.getBasePrice());
        product.setOriginalPrice(request.getOriginalPrice());
        product.setAvailableSizes(
                request.getAvailableSizes() != null ? request.getAvailableSizes() : "S,M,L,XL,XXL");
        product.setAvailableColors(
                request.getAvailableColors() != null ? request.getAvailableColors() : "White,Black,Gray,Navy");
        product.setStockQuantity(
                request.getStockQuantity() != null ? request.getStockQuantity() : 0);
    }

    private ProductDetailResponse toDetailResponse(Product product) {
        ProductDetailResponse dto = new ProductDetailResponse();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setMaterial(product.getMaterial());
        dto.setBasePrice(product.getBasePrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setAvailableSizes(product.getAvailableSizes());
        dto.setAvailableColors(product.getAvailableColors());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setUpdatedAt(product.getUpdatedAt());

        dto.setImages(
                productImageRepository.findByProductIdOrderBySortOrderAsc(product.getId())
                        .stream().map(this::toImageResponse).collect(Collectors.toList())
        );

        dto.setAverageRating(productRepository.findAverageRating(product.getId()));
        dto.setReviewCount(productRepository.countReviews(product.getId()));

        return dto;
    }

    private ProductListResponse toListResponse(Product product) {
        ProductListResponse dto = new ProductListResponse();
        dto.setId(product.getId());
        dto.setCategoryId(product.getCategory().getId());
        dto.setCategoryName(product.getCategory().getName());
        dto.setName(product.getName());
        dto.setBasePrice(product.getBasePrice());
        dto.setOriginalPrice(product.getOriginalPrice());
        dto.setStockQuantity(product.getStockQuantity());
        dto.setIsActive(product.getIsActive());
        dto.setCreatedAt(product.getCreatedAt());
        dto.setAverageRating(productRepository.findAverageRating(product.getId()));
        dto.setReviewCount(productRepository.countReviews(product.getId()));

        // Ảnh mặc định
        productImageRepository.findByProductIdAndIsDefaultTrue(product.getId())
                .stream().findFirst()
                .ifPresentOrElse(
                        img -> dto.setDefaultImageUrl(img.getImageUrl()),
                        () -> productImageRepository.findByProductIdOrderBySortOrderAsc(product.getId())
                                .stream().findFirst()
                                .ifPresent(img -> dto.setDefaultImageUrl(img.getImageUrl()))
                );

        return dto;
    }

    private ProductImageResponse toImageResponse(ProductImage img) {
        ProductImageResponse dto = new ProductImageResponse();
        dto.setId(img.getId());
        dto.setImageUrl(img.getImageUrl());
        dto.setImageType(img.getImageType());
        dto.setColor(img.getColor());
        dto.setSortOrder(img.getSortOrder());
        dto.setIsDefault(img.getIsDefault());
        return dto;
    }
}