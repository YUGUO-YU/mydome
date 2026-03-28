package com.blog.repository;

import com.blog.entity.ArticleLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

    Optional<ArticleLike> findByArticleIdAndUserId(Long articleId, Long userId);

    Long countByArticleId(Long articleId);

    boolean existsByArticleIdAndUserId(Long articleId, Long userId);
}
