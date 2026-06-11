package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Designs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "UserId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    @Column(name = "DesignName")
    private String designName;

    @Column(name = "PreviewUrl")
    private String previewUrl;

    @Column(name = "DesignData", columnDefinition = "nvarchar(max)")
    private String designData;

    @Column(name = "Size")
    private String size;

    @Column(name = "Color")
    private String color;

    @Column(name = "IsPublic")
    private Boolean isPublic;

    @Column(name = "LikeCount")
    private Integer likeCount;

    @Column(name = "ViewCount")
    private Integer viewCount;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "design")
    private List<DesignInteraction> interactions = new ArrayList<>();

    @OneToMany(mappedBy = "design")
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "design")
    private List<OrderItem> orderItems = new ArrayList<>();
}