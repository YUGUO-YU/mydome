package com.blog.service;

import com.blog.entity.Article;
import com.blog.entity.ArticleLike;
import com.blog.entity.User;
import com.blog.repository.ArticleLikeRepository;
import com.blog.repository.ArticleRepository;
import com.blog.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {

    @Autowired
    private ArticleLikeRepository likeRepository;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private UserRepository userRepository;

    public boolean hasLiked(Long articleId, Long userId) {
        return likeRepository.existsByArticleIdAndUserId(articleId, userId);
    }

    @Transactional
    public void likeArticle(Long articleId, Long userId) {
        if (likeRepository.existsByArticleIdAndUserId(articleId, userId)) {
            return; // Already liked
        }

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("文章不存在"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        ArticleLike like = new ArticleLike();
        like.setArticle(article);
        like.setUser(user);
        likeRepository.save(like);

        article.setLikeCount(article.getLikeCount() + 1);
        articleRepository.save(article);
    }

    @Transactional
    public void unlikeArticle(Long articleId, Long userId) {
        ArticleLike like = likeRepository.findByArticleIdAndUserId(articleId, userId)
                .orElse(null);

        if (like != null) {
            Article article = like.getArticle();
            if (article != null) {
                article.setLikeCount(Math.max(0, article.getLikeCount() - 1));
                articleRepository.save(article);
            }
            likeRepository.delete(like);
        }
    }
}
