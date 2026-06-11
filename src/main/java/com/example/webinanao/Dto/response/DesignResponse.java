package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DesignResponse {
    private Integer id;
    private Integer userId;
    private String userFullName;
    private Integer productId;
    private String productName;
    private String designName;
    private String previewUrl;
    private String designData;
    private String size;
    private String color;
    private Boolean isPublic;
    private Integer likeCount;
    private Integer viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
