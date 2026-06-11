package com.example.webinanao.Repo;

import com.example.webinanao.Entity.DiscountCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiscountCodeRepository extends JpaRepository<DiscountCode, Integer> {
    Optional<DiscountCode> findByCode(String code);
    Optional<DiscountCode> findByCodeAndIsActiveTrue(String code);
}
