package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

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

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(name = "rating")
    private Byte rating;

    @Column(name = "title")
    private String title;

    @Column(name = "comment", length = 1000)
    private String comment;

    @Column(name = "image_urls")
    private String imageUrls;

    @Column(name = "is_approved")
    private Boolean isApproved;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}