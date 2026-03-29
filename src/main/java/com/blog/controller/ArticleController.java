package com.blog.controller;

import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.service.ArticleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @Autowired
    private ArticleRepository articleRepository;

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getArticles(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        List<ArticleDTO> articles;
        if (categoryId != null) {
            articles = articleService.getArticlesByCategory(categoryId, page, size);
        } else {
            articles = articleService.getArticles(page, size);
        }
        return ResponseEntity.ok(articles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArticleDTO> getArticle(@PathVariable Long id) {
        return ResponseEntity.ok(articleService.getArticleById(id));
    }

    @PostMapping
    public ResponseEntity<ArticleDTO> createArticle(
            @RequestBody ArticleDTO dto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(articleService.createArticle(dto, user.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Long id,
            @RequestBody ArticleDTO dto,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // P0: 检查权限 - 只有作者才能修改
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("无权限修改他人的文章");
        }
        
        return ResponseEntity.ok(articleService.updateArticle(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteArticle(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // P0: 检查权限 - 只有作者才能删除
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        if (!article.getAuthor().getId().equals(user.getId())) {
            throw new RuntimeException("无权限删除他人的文章");
        }
        
        articleService.deleteArticle(id);
        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }

    @GetMapping("/popular")
    public ResponseEntity<List<ArticleDTO>> getPopularArticles(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(articleService.getPopularArticles(limit));
    }
}
