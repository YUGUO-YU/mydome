package com.blog.controller;

import com.blog.entity.User;
import com.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PutMapping("/profile")
    public ResponseEntity<Map<String, Object>> updateProfile(
            @RequestBody Map<String, String> data,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        User dbUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (data.containsKey("nickname")) {
            dbUser.setNickname(data.get("nickname"));
        }
        if (data.containsKey("email")) {
            dbUser.setEmail(data.get("email"));
        }
        if (data.containsKey("avatar")) {
            dbUser.setAvatar(data.get("avatar"));
        }
        
        userRepository.save(dbUser);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "保存成功");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/password")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestBody Map<String, String> data,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        String oldPassword = data.get("oldPassword");
        String newPassword = data.get("newPassword");
        
        User dbUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (!passwordEncoder.matches(oldPassword, dbUser.getPassword())) {
            throw new RuntimeException("当前密码错误");
        }
        
        dbUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(dbUser);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "密码修改成功");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/articles")
    public ResponseEntity<List<Map<String, Object>>> getUserArticles(
            @RequestParam Long userId,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // Only allow users to view their own articles
        if (!user.getId().equals(userId)) {
            throw new RuntimeException("无权限查看他人的文章");
        }
        
        List<Map<String, Object>> articles = user.getArticles().stream()
            .map(a -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", a.getId());
                map.put("title", a.getTitle());
                map.put("summary", a.getSummary());
                map.put("status", a.getStatus());
                map.put("viewCount", a.getViewCount());
                map.put("likeCount", a.getLikeCount());
                map.put("commentCount", a.getCommentCount());
                map.put("createdAt", a.getCreatedAt());
                return map;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(articles);
    }
}
