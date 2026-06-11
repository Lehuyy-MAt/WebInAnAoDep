package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    @Column(name = "Name",columnDefinition = "nvarchar(255)")
    private String name;

    @Column(name = "Description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "Material")
    private String material;

    @Column(name = "BasePrice")
    private BigDecimal basePrice;

    @Column(name = "OriginalPrice")
    private BigDecimal originalPrice;

    @Column(name = "AvailableSizes")
    private String availableSizes;

    @Column(name = "AvailableColors")
    private String availableColors;

    @Column(name = "StockQuantity")
    private Integer stockQuantity;

    @Column(name = "IsActive")
    private Boolean isActive = true;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "product")
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<Design> designs = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems = new ArrayList<>();

    @OneToMany(mappedBy = "product")
    private List<ProductImage> images = new ArrayList<>();

    // 👉 THÊM QUAN HỆ VỚI PRODUCT_IMAGE
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductImage> productImages = new ArrayList<>();

    // Helper method để thêm ảnh
    public void addProductImage(ProductImage image) {
        productImages.add(image);
        image.setProduct(this);
    }

}