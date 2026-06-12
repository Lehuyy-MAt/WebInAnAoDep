package com.example.webinanao.Controller;

import com.example.webinanao.Dto.response.HomeResponse;
import com.example.webinanao.Service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping
    public ResponseEntity<HomeResponse> getHomeData() {
        return ResponseEntity.ok(homeService.getHomeData());
    }
}