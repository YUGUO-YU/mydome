package com.blog.controller;

import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import com.blog.service.CommentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/article/{articleId}")
    public ResponseEntity<List<CommentDTO>> getComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(commentService.getCommentsByArticle(articleId, pageable));
    }

    @PostMapping
    public ResponseEntity<CommentDTO> createComment(
            @RequestBody CommentDTO dto,
            Authentication authentication) {
        // Try to get user from authentication first, then fall back to parameter
        Long userId = null;
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            userId = ((User) authentication.getPrincipal()).getId();
        }
        
        if (userId == null) {
            throw new RuntimeException("请先登录");
        }
        
        return ResponseEntity.ok(commentService.createComment(dto, userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteComment(
            @PathVariable Long id,
            Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        
        // Only allow users to delete their own comments
        Comment comment = commentService.getCommentById(id);
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权限删除他人的评论");
        }
        
        commentService.deleteComment(id);
        Map<String, String> result = new HashMap<>();
        result.put("message", "删除成功");
        return ResponseEntity.ok(result);
    }
}
