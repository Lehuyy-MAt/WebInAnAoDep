package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.UserUpdateRequest;
import com.example.webinanao.Dto.response.PageResponse;
import com.example.webinanao.Dto.response.UserResponse;
import com.example.webinanao.Service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;

    @GetMapping("/list")
    public ResponseEntity<PageResponse<UserResponse>> getUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getUsers(keyword, role, page, size));
    }

    @PutMapping("/toggle-status/{id}")
    public ResponseEntity<Void> toggleStatus(@PathVariable Integer id) {
        userService.toggleStatus(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/change-role/{id}")
    public ResponseEntity<Void> changeRole(
            @PathVariable Integer id,
            @RequestParam String role
    ) {
        userService.changeRole(id, role);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable Integer id,
            @RequestBody UserUpdateRequest request
    ) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}