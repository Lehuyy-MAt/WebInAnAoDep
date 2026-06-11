package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ReviewResponse {
    private Integer id;
    private Integer userId;
    private String userFullName;
    private String userAvatarUrl;
    private Integer productId;
    private String productName;
    private Integer orderId;
    private Byte rating;
    private String title;
    private String comment;
    private String imageUrls; // JSON array string
    private Boolean isApproved;
    private LocalDateTime createdAt;
}
