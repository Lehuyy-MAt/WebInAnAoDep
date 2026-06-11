package com.example.webinanao.Service;

import com.example.webinanao.Dto.request.ChangePasswordRequest;
import com.example.webinanao.Dto.request.UserUpdateRequest;
import com.example.webinanao.Dto.response.PageResponse;
import com.example.webinanao.Dto.response.UserResponse;
import com.example.webinanao.Entity.Role;
import com.example.webinanao.Entity.User;
import com.example.webinanao.Repo.RoleRepository;
import com.example.webinanao.Repo.UserRepository;
import com.example.webinanao.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;  // ← THÊM
    private final ImageUploadService imageUploadService;  // ← THÊM (nếu dùng Cloudinary)

    public PageResponse<UserResponse> getUsers(String keyword, String role, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> userPage;

        if (keyword != null && !keyword.isEmpty()) {
            userPage = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(keyword, keyword, pageable);
        } else if (role != null && !role.isEmpty()) {
            userPage = userRepository.findByRoleName(role, pageable);
        } else {
            userPage = userRepository.findAll(pageable);
        }

        var content = userPage.getContent().stream()
                .map(this::toUserResponse)
                .collect(Collectors.toList());

        return PageResponse.from(userPage, content);
    }

    @Transactional
    public void toggleStatus(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setIsActive(!user.getIsActive());
        userRepository.save(user);
    }

    @Transactional
    public void changeRole(Integer id, String roleName) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Role newRole = roleRepository.findByName(roleName)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found: " + roleName));

        user.setRole(newRole);
        userRepository.save(user);
    }

    @Transactional
    public UserResponse updateUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());

        user.setUpdatedAt(LocalDateTime.now());

        return toUserResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        userRepository.delete(user);
    }

    // ========== PHƯƠNG THỨC MỚI CHO PROFILE ==========

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return toUserResponse(user);
    }

    @Transactional
    public UserResponse updateUserByEmail(String email, UserUpdateRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhoneNumber() != null) user.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) user.setAddress(request.getAddress());

        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return toUserResponse(user);
    }

    @Transactional
    public void changePassword(String email, ChangePasswordRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // Dùng oldPassword thay vì currentPassword
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng");
        }

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    @Transactional
    public String uploadAvatar(String email, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        String avatarUrl = imageUploadService.uploadImage(file);
        user.setAvatarUrl(avatarUrl);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);

        return avatarUrl;
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(Long.valueOf(user.getId()))
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phone(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .address(user.getAddress())
                .isActive(user.getIsActive())
                .roles(Collections.singleton(user.getRole().getName()))
                .createdAt(user.getCreatedAt())
                .build();
    }
}