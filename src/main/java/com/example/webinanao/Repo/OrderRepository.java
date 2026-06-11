package com.example.webinanao.Repo;

import com.example.webinanao.Entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    List<Order> findByUserId(Integer userId);
    List<Order> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Order> findAllByOrderByCreatedAtDesc();
    List<Order> findTop5ByOrderByCreatedAtDesc();
}
