package com.blog.repository;

import com.blog.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    Page<Article> findByStatus(String status, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = :status ORDER BY a.createdAt DESC")
    List<Article> findByStatusOrderByCreatedAtDesc(@Param("status") String status, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.category.id = :categoryId AND a.status = :status ORDER BY a.createdAt DESC")
    List<Article> findByCategoryIdAndStatusOrderByCreatedAtDesc(
        @Param("categoryId") Long categoryId, 
        @Param("status") String status, 
        Pageable pageable);

    Page<Article> findByCategoryId(Long categoryId, Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.viewCount DESC")
    List<Article> findTopByViewCount(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.status = 'PUBLISHED' ORDER BY a.likeCount DESC")
    List<Article> findTopByLikeCount(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.isTop = true AND a.status = 'PUBLISHED'")
    List<Article> findTopArticles(Pageable pageable);

    @Query("SELECT a FROM Article a WHERE a.author.id = :authorId ORDER BY a.createdAt DESC")
    Page<Article> findByAuthorId(@Param("authorId") Long authorId, Pageable pageable);
}
