package com.example.webinanao.Repo;

import com.example.webinanao.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    List<Review> findByProductId(Integer productId);
    List<Review> findByProductIdAndIsApprovedTrueOrderByCreatedAtDesc(Integer productId);
    List<Review> findByUserId(Integer userId);
    List<Review> findByUserIdOrderByCreatedAtDesc(Integer userId);
    boolean existsByUserIdAndProductIdAndOrderId(Integer userId, Integer productId, Integer orderId);
}
