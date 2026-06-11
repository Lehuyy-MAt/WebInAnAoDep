package com.example.webinanao.Dto.response;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DiscountCodeResponse {
    private Integer id;
    private String code;
    private String discountType; // Percentage or FixedAmount
    private BigDecimal discountValue;
    private BigDecimal minOrderAmount;
    private BigDecimal maxDiscount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isValid; // computed: active + within date + usage not exceeded
    private String message;  // e.g. "Mã hợp lệ" hoặc lý do không hợp lệ
}
