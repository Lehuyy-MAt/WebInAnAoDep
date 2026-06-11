package com.example.webinanao.Controller;

import com.example.webinanao.Dto.response.DiscountCodeResponse;
import com.example.webinanao.Service.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/discounts")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class DiscountController {

    private final DiscountService discountService;

    // Kiểm tra mã giảm giá có hợp lệ không
    @GetMapping("/check")
    public ResponseEntity<DiscountCodeResponse> checkDiscount(@RequestParam String code) {
        return ResponseEntity.ok(discountService.checkDiscount(code));
    }
}