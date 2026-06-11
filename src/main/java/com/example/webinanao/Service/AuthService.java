package com.example.webinanao.Service;

import com.example.webinanao.Dto.request.LoginRequest;
import com.example.webinanao.Dto.request.RegisterRequest;
import com.example.webinanao.Dto.response.AuthResponse;
import com.example.webinanao.Entity.Role;
import com.example.webinanao.Entity.User;
import com.example.webinanao.Repo.RoleRepository;
import com.example.webinanao.Repo.UserRepository;
import com.example.webinanao.Security.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@EnableAsync
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenUtil jwtTokenUtil;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;  // ← Di chuyển lên đây, trước các method

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtTokenUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName(),
                user.getId()
        );
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email đã tồn tại");
        }

        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Role USER not found"));

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setPhoneNumber(request.getPhone());
        user.setRole(role);
        user.setIsActive(true);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        String token = jwtTokenUtil.generateToken(user.getEmail());

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getFullName(),
                user.getRole().getName(),
                user.getId()
        );
    }


    @Async
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String resetToken = jwtTokenUtil.generateResetToken(user.getEmail());
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;

        emailService.sendResetPasswordEmail(email, resetLink);

        System.out.println("Reset link sent to: " + email);
        System.out.println("Link: " + resetLink);
    }

    public void resetPassword(String token, String newPassword) {
        String email = jwtTokenUtil.getEmailFromToken(token);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }
}
