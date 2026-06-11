package com.example.webinanao.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ProductImages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ProductId")
    private Product product;

    @Column(name = "ImageUrl")
    private String imageUrl;

    @Column(name = "ImageType")
    private String imageType;

    @Column(name = "Color")
    private String color;

    @Column(name = "SortOrder")
    private Integer sortOrder;

    @Column(name = "IsDefault")
    private Boolean isDefault;
}