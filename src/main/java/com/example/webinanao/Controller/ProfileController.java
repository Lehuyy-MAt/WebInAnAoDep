package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.ChangePasswordRequest;
import com.example.webinanao.Dto.request.UserUpdateRequest;
import com.example.webinanao.Dto.response.UserResponse;
import com.example.webinanao.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserResponse> updateProfile(
            @RequestBody UserUpdateRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return ResponseEntity.ok(userService.updateUserByEmail(email, request));
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @RequestBody ChangePasswordRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        userService.changePassword(email, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        String email = authentication.getName();
        String avatarUrl = userService.uploadAvatar(email, file);
        return ResponseEntity.ok(java.util.Map.of("avatarUrl", avatarUrl));
    }
}