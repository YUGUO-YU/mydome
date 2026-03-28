package com.blog.service;

import com.blog.dto.CommentDTO;
import com.blog.entity.Article;
import com.blog.entity.Comment;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CommentRepository;
import com.blog.repository.UserRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    public List<CommentDTO> getCommentsByArticle(Long articleId, Pageable pageable) {
        Page<Comment> comments = commentRepository.findByArticleId(articleId, pageable);
        return comments.stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Transactional
    public CommentDTO createComment(CommentDTO dto, Long userId) {
        Comment comment = new Comment();
        
        Article article = articleRepository.findById(dto.getArticleId())
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        comment.setArticle(article);
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        comment.setUser(user);
        
        comment.setContent(dto.getContent());
        comment.setParentId(dto.getParentId());
        
        comment = commentRepository.save(comment);
        
        // Update article comment count
        article.setCommentCount(article.getCommentCount() + 1);
        articleRepository.save(article);
        
        return toDTO(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("评论不存在"));
        
        // Update article comment count
        Article article = comment.getArticle();
        if (article != null) {
            article.setCommentCount(Math.max(0, article.getCommentCount() - 1));
            articleRepository.save(article);
        }
        
        commentRepository.deleteById(id);
    }

    private CommentDTO toDTO(Comment comment) {
        CommentDTO dto = new CommentDTO();
        BeanUtils.copyProperties(comment, dto);
        
        if (comment.getArticle() != null) {
            dto.setArticleId(comment.getArticle().getId());
        }
        
        if (comment.getUser() != null) {
            dto.setUserId(comment.getUser().getId());
            dto.setUsername(comment.getUser().getUsername());
            dto.setNickname(comment.getUser().getNickname());
            dto.setAvatar(comment.getUser().getAvatar());
        }
        
        return dto;
    }
}
