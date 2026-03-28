package com.blog.controller;

import com.blog.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{articleId}")
    public ResponseEntity<Map<String, Object>> likeArticle(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Long userId) {
        likeService.likeArticle(articleId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", true);
        result.put("message", "点赞成功");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{articleId}")
    public ResponseEntity<Map<String, Object>> unlikeArticle(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Long userId) {
        likeService.unlikeArticle(articleId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("liked", false);
        result.put("message", "取消点赞成功");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{articleId}/status")
    public ResponseEntity<Map<String, Boolean>> getLikeStatus(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") Long userId) {
        boolean hasLiked = likeService.hasLiked(articleId, userId);
        Map<String, Boolean> result = new HashMap<>();
        result.put("liked", hasLiked);
        return ResponseEntity.ok(result);
    }
}
