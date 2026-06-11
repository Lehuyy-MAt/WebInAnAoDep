package com.example.webinanao.config;

import com.example.webinanao.Entity.Role;
import com.example.webinanao.Repo.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Tạo role ADMIN nếu chưa có
        if (roleRepository.findByName("ADMIN").isEmpty()) {
            Role adminRole = new Role();
            adminRole.setName("ADMIN");
            adminRole.setDescription("Quản trị viên");
            adminRole.setIsActive(true);
            roleRepository.save(adminRole);
            System.out.println("✅ Created role: ADMIN");
        }

        // SỬA: CUSTOMER -> USER
        if (roleRepository.findByName("USER").isEmpty()) {
            Role userRole = new Role();
            userRole.setName("USER");
            userRole.setDescription("Khách hàng");
            userRole.setIsActive(true);
            roleRepository.save(userRole);
            System.out.println("✅ Created role: USER");
        }
    }
}