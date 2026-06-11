package com.example.webinanao.Repo;


import com.example.webinanao.Entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
}