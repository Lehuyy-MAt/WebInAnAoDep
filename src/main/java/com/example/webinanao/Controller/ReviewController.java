package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.ReviewRequest;
import com.example.webinanao.Dto.response.ReviewResponse;
import com.example.webinanao.Service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // Lấy review theo sản phẩm
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(reviewService.getReviewsByProductId(productId));
    }

    // Lấy review của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(reviewService.getReviewsByUserId(userId));
    }

    // Gửi review
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(reviewService.createReview(request));
    }

    // Xóa review
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    // Admin: duyệt review
    @PutMapping("/{reviewId}/approve")
    public ResponseEntity<ReviewResponse> approveReview(@PathVariable Integer reviewId) {
        return ResponseEntity.ok(reviewService.approveReview(reviewId));
    }
}