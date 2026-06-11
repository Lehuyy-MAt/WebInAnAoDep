package com.example.webinanao.Repo;

import com.example.webinanao.Entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProductIdOrderBySortOrderAsc(Integer productId);

    List<ProductImage> findByProductId(Integer productId);

    Optional<ProductImage> findByProductIdAndIsDefaultTrue(Integer productId);



    // Bỏ isDefault của tất cả ảnh của product (trước khi set ảnh mới làm default)
    @Modifying
    @Query("UPDATE ProductImage pi SET pi.isDefault = false WHERE pi.product.id = :productId")
    void clearDefaultByProductId(@Param("productId") Integer productId);
}
