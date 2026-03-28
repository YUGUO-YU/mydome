package com.blog.controller;

import com.blog.dto.ArticleDTO;
import com.blog.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {

    @Autowired
    private ArticleService articleService;

    @GetMapping
    public ResponseEntity<List<ArticleDTO>> getArticles(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        List<ArticleDTO> articles;
        if (categoryId != null) {
            articles = articleService.getArticlesByCategory(categoryId, pageable);
        } else {
            articles = articleService.getArticles(pageable);
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
            @RequestParam(defaultValue = "1") Long authorId) {
        return ResponseEntity.ok(articleService.createArticle(dto, authorId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArticleDTO> updateArticle(
            @PathVariable Long id,
            @RequestBody ArticleDTO dto) {
        return ResponseEntity.ok(articleService.updateArticle(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteArticle(@PathVariable Long id) {
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
