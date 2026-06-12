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
@Table(name = "designs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Design {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "Design_name")
    private String designName;

    @Column(name = "preview_url")
    private String previewUrl;

    @Column(name = "design_data")
    private String designData;

    @Column(name = "size")
    private String size;

    @Column(name = "color")
    private String color;

    @Column(name = "is_public")
    private Boolean isPublic;

    @Column(name = "like_count")
    private Integer likeCount;

    @Column(name = "view_count")
    private Integer viewCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "design")
    private List<DesignInteraction> interactions = new ArrayList<>();

    @OneToMany(mappedBy = "design")
    private List<CartItem> cartItems = new ArrayList<>();

    @OneToMany(mappedBy = "design")
    private List<OrderItem> orderItems = new ArrayList<>();
}