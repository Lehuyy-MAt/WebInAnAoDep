package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Review {

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

    @ManyToOne
    @JoinColumn(name = "OrderId")
    private Order order;

    @Column(name = "Rating")
    private Byte rating;

    @Column(name = "Title")
    private String title;

    @Column(name = "Comment", length = 1000)
    private String comment;

    @Column(name = "ImageUrls", columnDefinition = "nvarchar(max)")
    private String imageUrls;

    @Column(name = "IsApproved")
    private Boolean isApproved;

    @Column(name = "CreatedAt")
    private LocalDateTime createdAt;
}