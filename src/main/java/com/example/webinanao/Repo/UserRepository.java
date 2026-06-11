package com.example.webinanao.Repo;


import com.example.webinanao.Entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName, String email, Pageable pageable);

    Page<User> findByRoleName(String roleName, Pageable pageable);
}