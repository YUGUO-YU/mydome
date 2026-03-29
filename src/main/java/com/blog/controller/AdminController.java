package com.blog.controller;

import com.blog.entity.*;
import com.blog.repository.*;
import com.blog.service.SystemSettingService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private OperationLogRepository logRepository;

    @Autowired
    private SystemSettingService systemSettingService;

    // ==================== 用户管理 ====================

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String keyword) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<User> users;
        
        if (keyword != null && !keyword.isEmpty()) {
            // 简单实现：按用户名搜索
            users = userRepository.findByUsernameContaining(keyword, pageRequest);
        } else if (status != null) {
            users = userRepository.findByStatus(status, pageRequest);
        } else if (role != null) {
            users = userRepository.findByRole(role, pageRequest);
        } else {
            users = userRepository.findAll(pageRequest);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", users.getContent());
        result.put("totalElements", users.getTotalElements());
        result.put("totalPages", users.getTotalPages());
        
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        return ResponseEntity.ok(userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在")));
    }

    @PutMapping("/users/{id}/status")
    public ResponseEntity<Map<String, Object>> updateUserStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> data,
            Authentication auth,
            HttpServletRequest request) {
        
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        String oldStatus = user.getStatus();
        user.setStatus(data.get("status"));
        userRepository.save(user);
        
        // 记录日志
        saveLog(auth, "UPDATE_USER_STATUS", "USER", id, user.getUsername(), 
                "修改用户状态: " + oldStatus + " -> " + data.get("status"), request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("status", user.getStatus());
        return ResponseEntity.ok(result);
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Map<String, Object>> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> data,
            Authentication auth,
            HttpServletRequest request) {
        
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        String oldRole = user.getRole();
        user.setRole(data.get("role"));
        userRepository.save(user);
        
        // 记录日志
        saveLog(auth, "UPDATE_USER_ROLE", "USER", id, user.getUsername(),
                "修改用户角色: " + oldRole + " -> " + data.get("role"), request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("role", user.getRole());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(
            @PathVariable Long id,
            Authentication auth,
            HttpServletRequest request) {
        
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 记录日志
        saveLog(auth, "DELETE_USER", "USER", id, user.getUsername(),
                "删除用户: " + user.getUsername(), request);
        
        userRepository.deleteById(id);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }

    // ==================== 文章管理 ====================

    @GetMapping("/articles")
    public ResponseEntity<Map<String, Object>> getAllArticles(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long authorId) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Article> articles;
        
        if (categoryId != null) {
            articles = articleRepository.findByCategoryId(categoryId, pageRequest);
        } else if (status != null) {
            articles = articleRepository.findByStatus(status, pageRequest);
        } else if (authorId != null) {
            articles = articleRepository.findByAuthorId(authorId, pageRequest);
        } else {
            articles = articleRepository.findAll(pageRequest);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("content", articles.getContent());
        result.put("totalElements", articles.getTotalElements());
        result.put("totalPages", articles.getTotalPages());
        
        return ResponseEntity.ok(result);
    }

    @PutMapping("/articles/{id}")
    public ResponseEntity<Article> updateArticle(
            @PathVariable Long id,
            @RequestBody Article articleUpdate,
            Authentication auth,
            HttpServletRequest request) {
        
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("文章不存在"));
        
        if (articleUpdate.getTitle() != null) article.setTitle(articleUpdate.getTitle());
        if (articleUpdate.getContent() != null) article.setContent(articleUpdate.getContent());
        if (articleUpdate.getSummary() != null) article.setSummary(articleUpdate.getSummary());
        if (articleUpdate.getStatus() != null) article.setStatus(articleUpdate.getStatus());
        if (articleUpdate.getIsTop() != null) article.setIsTop(articleUpdate.getIsTop());
        
        article = articleRepository.save(article);
        
        // 记录日志
        saveLog(auth, "UPDATE_ARTICLE", "ARTICLE", id, article.getTitle(),
                "修改文章: " + article.getTitle(), request);
        
        return ResponseEntity.ok(article);
    }

    @DeleteMapping("/articles/{id}")
    public ResponseEntity<Map<String, String>> deleteArticle(
            @PathVariable Long id,
            Authentication auth,
            HttpServletRequest request) {
        
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("文章不存在"));
        
        // 记录日志
        saveLog(auth, "DELETE_ARTICLE", "ARTICLE", id, article.getTitle(),
                "删除文章: " + article.getTitle(), request);
        
        articleRepository.deleteById(id);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }

    @PutMapping("/articles/{id}/top")
    public ResponseEntity<Map<String, Object>> toggleTop(
            @PathVariable Long id,
            Authentication auth,
            HttpServletRequest request) {
        
        Article article = articleRepository.findById(id).orElseThrow(() -> new RuntimeException("文章不存在"));
        article.setIsTop(!article.getIsTop());
        articleRepository.save(article);
        
        // 记录日志
        saveLog(auth, "TOGGLE_ARTICLE_TOP", "ARTICLE", id, article.getTitle(),
                (article.getIsTop() ? "置顶" : "取消置顶") + "文章: " + article.getTitle(), request);
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", article.getId());
        result.put("isTop", article.getIsTop());
        return ResponseEntity.ok(result);
    }

    // ==================== 分类管理 ====================

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    @PostMapping("/categories")
    public ResponseEntity<Category> createCategory(
            @RequestBody Category category,
            Authentication auth,
            HttpServletRequest request) {
        
        Category saved = categoryRepository.save(category);
        
        // 记录日志
        saveLog(auth, "CREATE_CATEGORY", "CATEGORY", saved.getId(), saved.getName(),
                "创建分类: " + saved.getName(), request);
        
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Category> updateCategory(
            @PathVariable Long id,
            @RequestBody Category categoryUpdate,
            Authentication auth,
            HttpServletRequest request) {
        
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
        
        if (categoryUpdate.getName() != null) category.setName(categoryUpdate.getName());
        if (categoryUpdate.getDescription() != null) category.setDescription(categoryUpdate.getDescription());
        
        category = categoryRepository.save(category);
        
        // 记录日志
        saveLog(auth, "UPDATE_CATEGORY", "CATEGORY", id, category.getName(),
                "修改分类: " + category.getName(), request);
        
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Map<String, String>> deleteCategory(
            @PathVariable Long id,
            Authentication auth,
            HttpServletRequest request) {
        
        Category category = categoryRepository.findById(id).orElseThrow(() -> new RuntimeException("分类不存在"));
        
        // 记录日志
        saveLog(auth, "DELETE_CATEGORY", "CATEGORY", id, category.getName(),
                "删除分类: " + category.getName(), request);
        
        categoryRepository.deleteById(id);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }

    // ==================== 评论管理 ====================

    @GetMapping("/comments")
    public ResponseEntity<Map<String, Object>> getComments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<Comment> comments = commentRepository.findAll(pageRequest);
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", comments.getContent());
        result.put("totalElements", comments.getTotalElements());
        result.put("totalPages", comments.getTotalPages());
        
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long id,
            Authentication auth,
            HttpServletRequest request) {
        
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new RuntimeException("评论不存在"));
        
        // 记录日志
        saveLog(auth, "DELETE_COMMENT", "COMMENT", id, comment.getContent(),
                "删除评论: " + comment.getContent().substring(0, Math.min(50, comment.getContent().length())), request);
        
        commentRepository.deleteById(id);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }

    // ==================== 系统设置 ====================

    @GetMapping("/settings")
    public ResponseEntity<Map<String, String>> getSettings() {
        return ResponseEntity.ok(systemSettingService.getAllSettings());
    }

    @PutMapping("/settings")
    public ResponseEntity<Map<String, String>> updateSettings(
            @RequestBody Map<String, String> settings,
            Authentication auth,
            HttpServletRequest request) {
        
        systemSettingService.updateSettings(settings);
        
        // 记录日志
        saveLog(auth, "UPDATE_SETTINGS", "SYSTEM", null, null,
                "更新系统设置", request);
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "设置更新成功");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/settings/{key}")
    public ResponseEntity<Map<String, String>> getSetting(@PathVariable String key) {
        Map<String, String> result = new HashMap<>();
        result.put("key", key);
        result.put("value", systemSettingService.getSetting(key));
        return ResponseEntity.ok(result);
    }

    @PutMapping("/settings/{key}")
    public ResponseEntity<Map<String, String>> updateSetting(
            @PathVariable String key,
            @RequestBody Map<String, String> data,
            Authentication auth,
            HttpServletRequest request) {
        
        systemSettingService.updateSetting(key, data.get("value"));
        
        Map<String, String> result = new HashMap<>();
        result.put("message", "设置更新成功");
        return ResponseEntity.ok(result);
    }

    // ==================== 操作日志 ====================

    @GetMapping("/logs")
    public ResponseEntity<Map<String, Object>> getLogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String operation) {
        
        PageRequest pageRequest = PageRequest.of(page, size);
        Page<OperationLog> logs;
        
        if (operation != null) {
            logs = logRepository.findByOperation(operation, pageRequest);
        } else {
            logs = logRepository.findAll(pageRequest);
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("content", logs.getContent());
        result.put("totalElements", logs.getTotalElements());
        result.put("totalPages", logs.getTotalPages());
        
        return ResponseEntity.ok(result);
    }

    // ==================== 辅助方法 ====================

    private void saveLog(Authentication auth, String operation, String targetType, 
                        Long targetId, String targetName, String detail, HttpServletRequest request) {
        try {
            User user = (User) auth.getPrincipal();
            OperationLog log = new OperationLog();
            log.setUserId(user.getId());
            log.setUsername(user.getUsername());
            log.setOperation(operation);
            log.setTargetType(targetType);
            log.setTargetId(targetId);
            log.setTargetName(targetName);
            log.setDetail(detail);
            log.setIpAddress(getClientIp(request));
            log.setUserAgent(request.getHeader("User-Agent"));
            logRepository.save(log);
        } catch (Exception e) {
            // 日志记录失败不影响主业务
        }
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
