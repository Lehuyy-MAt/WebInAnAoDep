package com.example.webinanao.Service;

import com.example.webinanao.Dto.response.DiscountCodeResponse;
import com.example.webinanao.Entity.DiscountCode;
import com.example.webinanao.Repo.DiscountCodeRepository;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final DiscountCodeRepository discountCodeRepository;

    public DiscountCodeResponse checkDiscount(String code) {
        DiscountCode discount = discountCodeRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Mã giảm giá không tồn tại: " + code));

        DiscountCodeResponse response = toDiscountCodeResponse(discount);

        // Kiểm tra tính hợp lệ
        LocalDateTime now = LocalDateTime.now();
        boolean isActive = Boolean.TRUE.equals(discount.getIsActive());
        boolean withinDate = (discount.getStartDate() == null || !now.isBefore(discount.getStartDate()))
                && (discount.getEndDate() == null || !now.isAfter(discount.getEndDate()));
        boolean usageOk = discount.getUsageLimit() == null
                || discount.getUsedCount() < discount.getUsageLimit();

        if (!isActive) {
            response.setIsValid(false);
            response.setMessage("Mã giảm giá không còn hoạt động");
        } else if (!withinDate) {
            response.setIsValid(false);
            response.setMessage("Mã giảm giá đã hết hạn hoặc chưa đến ngày áp dụng");
        } else if (!usageOk) {
            response.setIsValid(false);
            response.setMessage("Mã giảm giá đã đạt giới hạn sử dụng");
        } else {
            response.setIsValid(true);
            response.setMessage("Mã giảm giá hợp lệ");
        }

        return response;
    }

    private DiscountCodeResponse toDiscountCodeResponse(DiscountCode discount) {
        DiscountCodeResponse dto = new DiscountCodeResponse();
        dto.setId(discount.getId());
        dto.setCode(discount.getCode());
        dto.setDiscountType(discount.getDiscountType());
        dto.setDiscountValue(discount.getDiscountValue());
        dto.setMinOrderAmount(discount.getMinOrderAmount());
        dto.setMaxDiscount(discount.getMaxDiscount());
        dto.setUsageLimit(discount.getUsageLimit());
        dto.setUsedCount(discount.getUsedCount());
        dto.setStartDate(discount.getStartDate());
        dto.setEndDate(discount.getEndDate());
        return dto;
    }
}
