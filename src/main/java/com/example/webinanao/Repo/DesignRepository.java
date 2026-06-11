package com.example.webinanao.Repo;

import com.example.webinanao.Entity.Design;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DesignRepository extends JpaRepository<Design, Integer> {
    List<Design> findByUserId(Integer userId);
    List<Design> findByUserIdOrderByCreatedAtDesc(Integer userId);
    List<Design> findByIsPublicTrueOrderByCreatedAtDesc();
    List<Design> findByProductId(Integer productId);
}
