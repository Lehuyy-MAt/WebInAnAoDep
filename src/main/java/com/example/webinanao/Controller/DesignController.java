package com.example.webinanao.Controller;

import com.example.webinanao.Dto.request.DesignRequest;
import com.example.webinanao.Dto.response.DesignResponse;
import com.example.webinanao.Service.DesignService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/designs")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class DesignController {

    private final DesignService designService;

    // Lấy design công khai (để hiển thị cộng đồng)
    @GetMapping("/public")
    public ResponseEntity<List<DesignResponse>> getPublicDesigns() {
        return ResponseEntity.ok(designService.getPublicDesigns());
    }

    // Lấy design của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DesignResponse>> getDesignsByUser(@PathVariable Integer userId) {
        return ResponseEntity.ok(designService.getDesignsByUserId(userId));
    }

    // Xem chi tiết design
    @GetMapping("/{designId}")
    public ResponseEntity<DesignResponse> getDesignById(@PathVariable Integer designId) {
        return ResponseEntity.ok(designService.getDesignById(designId));
    }

    // Lưu thiết kế mới
    @PostMapping
    public ResponseEntity<DesignResponse> createDesign(@Valid @RequestBody DesignRequest request) {
        return ResponseEntity.ok(designService.createDesign(request));
    }

    // Cập nhật thiết kế
    @PutMapping("/{designId}")
    public ResponseEntity<DesignResponse> updateDesign(
            @PathVariable Integer designId,
            @Valid @RequestBody DesignRequest request) {
        return ResponseEntity.ok(designService.updateDesign(designId, request));
    }

    // Xóa thiết kế
    @DeleteMapping("/{designId}")
    public ResponseEntity<Void> deleteDesign(@PathVariable Integer designId) {
        designService.deleteDesign(designId);
        return ResponseEntity.noContent().build();
    }
}