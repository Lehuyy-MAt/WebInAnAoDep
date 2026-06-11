package com.example.webinanao.Security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
@RequiredArgsConstructor
@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    // KHÔNG dùng @RequiredArgsConstructor
    // Tạo ObjectMapper trực tiếp
    private ObjectMapper objectMapper = new ObjectMapper(); // Remove 'final'

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(
                objectMapper.writeValueAsString(Map.of(
                        "status", 401,
                        "message", "UNAUTHORIZED",
                        "path", request.getRequestURI()
                ))
        );
    }
}