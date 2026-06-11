package com.example.webinanao.Repo;

import com.example.webinanao.Entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // Home page
    List<Product> findTop8ByIsActiveTrueOrderByCreatedAtDesc();
    List<Product> findTop8ByIsActiveTrueOrderByIdDesc();

    // Lấy tất cả đang active
    List<Product> findByIsActiveTrue();

    // Lấy theo danh mục
    List<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId);

    // Phân trang + tìm kiếm + lọc
    @Query("""
        SELECT p FROM Product p
        WHERE p.isActive = true
          AND (:categoryId IS NULL OR p.category.id = :categoryId)
          AND (:keyword  IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:minPrice IS NULL OR p.basePrice >= :minPrice)
          AND (:maxPrice IS NULL OR p.basePrice <= :maxPrice)
    """)
    Page<Product> searchProducts(
            @Param("categoryId") Integer categoryId,
            @Param("keyword")    String keyword,
            @Param("minPrice")   BigDecimal minPrice,
            @Param("maxPrice")   BigDecimal maxPrice,
            Pageable pageable
    );

    // Admin: phân trang toàn bộ (kể cả inactive)
    @Query("""
        SELECT p FROM Product p
        WHERE (:keyword IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))
          AND (:isActive IS NULL OR p.isActive = :isActive)
    """)
    Page<Product> findAllForAdmin(
            @Param("keyword")  String keyword,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );

    // Rating trung bình của sản phẩm
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId AND r.isApproved = true")
    Double findAverageRating(@Param("productId") Integer productId);

    // Số lượng review
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.id = :productId AND r.isApproved = true")
    Integer countReviews(@Param("productId") Integer productId);
}
