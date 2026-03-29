package com.blog.controller;

import com.blog.config.JwtAuthFilter;
import com.blog.dto.AuthResponse;
import com.blog.dto.LoginRequest;
import com.blog.dto.RegisterRequest;
import com.blog.entity.User;
import com.blog.repository.UserRepository;
import com.blog.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        Map<String, String> result = new HashMap<>();
        result.put("message", "退出成功");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }
        
        try {
            String token = authHeader.substring(7);
            Long userId = JwtAuthFilter.getUserIdFromToken(token);
            User user = userRepository.findById(userId).orElse(null);
            
            if (user == null) {
                return ResponseEntity.status(401).body(null);
            }
            
            Map<String, Object> result = new HashMap<>();
            result.put("userId", user.getId());
            result.put("username", user.getUsername());
            result.put("nickname", user.getNickname());
            result.put("email", user.getEmail());
            result.put("avatar", user.getAvatar());
            result.put("role", user.getRole());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(401).body(null);
        }
    }

    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkUsername(@RequestParam String username) {
        boolean available = !userRepository.existsByUsername(username);
        Map<String, Boolean> result = new HashMap<>();
        result.put("available", available);
        return ResponseEntity.ok(result);
    }
}
