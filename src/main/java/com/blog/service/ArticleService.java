package com.blog.service;

import com.blog.dto.ArticleDTO;
import com.blog.entity.Article;
import com.blog.entity.Category;
import com.blog.entity.User;
import com.blog.repository.ArticleRepository;
import com.blog.repository.CategoryRepository;
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
public class ArticleService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private UserRepository userRepository;

    public List<ArticleDTO> getArticles(Pageable pageable) {
        Page<Article> articles = articleRepository.findByStatus("PUBLISHED", pageable);
        return articles.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<ArticleDTO> getArticlesByCategory(Long categoryId, Pageable pageable) {
        Page<Article> articles = articleRepository.findByCategoryId(categoryId, pageable);
        return articles.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ArticleDTO getArticleById(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        // Increase view count
        article.setViewCount(article.getViewCount() + 1);
        articleRepository.save(article);
        
        return toDTO(article);
    }

    @Transactional
    public ArticleDTO createArticle(ArticleDTO dto, Long authorId) {
        Article article = new Article();
        BeanUtils.copyProperties(dto, article);
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        }
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        article.setAuthor(author);
        
        article = articleRepository.save(article);
        return toDTO(article);
    }

    @Transactional
    public ArticleDTO updateArticle(Long id, ArticleDTO dto) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        
        if (dto.getTitle() != null) article.setTitle(dto.getTitle());
        if (dto.getContent() != null) article.setContent(dto.getContent());
        if (dto.getSummary() != null) article.setSummary(dto.getSummary());
        if (dto.getCoverImage() != null) article.setCoverImage(dto.getCoverImage());
        if (dto.getStatus() != null) article.setStatus(dto.getStatus());
        if (dto.getIsTop() != null) article.setIsTop(dto.getIsTop());
        
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new RuntimeException("分类不存在"));
            article.setCategory(category);
        }
        
        article = articleRepository.save(article);
        return toDTO(article);
    }

    @Transactional
    public void deleteArticle(Long id) {
        articleRepository.deleteById(id);
    }

    public List<ArticleDTO> getPopularArticles(int limit) {
        List<Article> articles = articleRepository.findTopByViewCount(Pageable.ofSize(limit));
        return articles.stream().map(this::toDTO).collect(Collectors.toList());
    }

    private ArticleDTO toDTO(Article article) {
        ArticleDTO dto = new ArticleDTO();
        BeanUtils.copyProperties(article, dto);
        
        if (article.getCategory() != null) {
            dto.setCategoryId(article.getCategory().getId());
            dto.setCategoryName(article.getCategory().getName());
        }
        
        if (article.getAuthor() != null) {
            dto.setAuthorId(article.getAuthor().getId());
            dto.setAuthorNickname(article.getAuthor().getNickname());
        }
        
        return dto;
    }
}
