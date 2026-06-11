package com.example.webinanao.Service;

import com.example.webinanao.Dto.request.ReviewRequest;
import com.example.webinanao.Dto.response.ReviewResponse;
import com.example.webinanao.Entity.*;
import com.example.webinanao.Repo.*;
import com.example.webinanao.exception.BadRequestException;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public List<ReviewResponse> getReviewsByProductId(Integer productId) {
        return reviewRepository.findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(productId)
                .stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByUserId(Integer userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toReviewResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public ReviewResponse createReview(ReviewRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new ResourceNotFoundException("Sản phẩm không tồn tại"));
        Order order = orderRepository.findById(request.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Đơn hàng không tồn tại"));

        // Kiểm tra đã review đơn hàng này chưa
        boolean alreadyReviewed = reviewRepository
                .existsByUserIdAndProductIdAndOrderId(request.getUserId(), request.getProductId(), request.getOrderId());
        if (alreadyReviewed) {
            throw new BadRequestException("Bạn đã đánh giá sản phẩm này cho đơn hàng này rồi");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setOrder(order);
        review.setRating(request.getRating());
        review.setTitle(request.getTitle());
        review.setComment(request.getComment());
        review.setImageUrls(request.getImageUrls());
        review.setIsApproved(false); // cần admin duyệt

        return toReviewResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại với id: " + reviewId));
        reviewRepository.delete(review);
    }

    @Transactional
    public ReviewResponse approveReview(Integer reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Đánh giá không tồn tại với id: " + reviewId));
        review.setIsApproved(true);
        return toReviewResponse(reviewRepository.save(review));
    }

    private ReviewResponse toReviewResponse(Review review) {
        ReviewResponse dto = new ReviewResponse();
        dto.setId(review.getId());
        dto.setUserId(review.getUser().getId());
        dto.setUserFullName(review.getUser().getFullName());
        dto.setUserAvatarUrl(review.getUser().getAvatarUrl());
        dto.setProductId(review.getProduct().getId());
        dto.setProductName(review.getProduct().getName());
        dto.setOrderId(review.getOrder().getId());
        dto.setRating(review.getRating());
        dto.setTitle(review.getTitle());
        dto.setComment(review.getComment());
        dto.setImageUrls(review.getImageUrls());
        dto.setIsApproved(review.getIsApproved());
        dto.setCreatedAt(review.getCreatedAt());
        return dto;
    }
}
