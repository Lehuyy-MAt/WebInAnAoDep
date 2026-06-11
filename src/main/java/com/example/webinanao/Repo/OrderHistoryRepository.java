package com.example.webinanao.Repo;

import com.example.webinanao.Entity.OrderHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderHistoryRepository extends JpaRepository<OrderHistory, Integer> {
    List<OrderHistory> findByOrderIdOrderByCreatedAtDesc(Integer orderId);
}