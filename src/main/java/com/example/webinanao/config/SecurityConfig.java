package com.example.webinanao.config;

import com.example.webinanao.Security.RestAuthenticationEntryPoint;
import com.example.webinanao.Security.TokenAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final RestAuthenticationEntryPoint entryPoint;

    private static final String[] URL_WHITELIST = {
            "/",                     // Mở khóa đường dẫn gốc để trình duyệt không báo 401 khi gõ domain
            "/api/auth/**",
            "/api/home",             // Thêm API trang chủ vào whitelist
            "/api/categories/public/**", // Đổi cấu hình categories công khai (xem ở dưới)
            "/api/products/public/**",
            "/api/designs/public/**",
            "/api/products/search",
            "/api/products/{id}",
            "/error"
    };

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(org.springframework.security.config.Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(URL_WHITELIST).permitAll()

                        // Cấu hình cụ thể cho Categories bảo mật:
                        // Cho phép GET (xem) công khai, nhưng POST, PUT, DELETE thì phải là ADMIN hoặc STAFF
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/categories").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/categories").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.PUT, "/api/categories/**").hasAnyRole("ADMIN", "STAFF")
                        .requestMatchers(org.springframework.http.HttpMethod.DELETE, "/api/categories/**").hasRole("ADMIN")

                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }



    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration config = new org.springframework.web.cors.CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:5173",
                "https://web-in-an-ao-dep-fe-w6d6.vercel.app",
                "https://web-in-an-ao-dep-fe-w6d6-*.vercel.app"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source =
                new org.springframework.web.cors.UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}