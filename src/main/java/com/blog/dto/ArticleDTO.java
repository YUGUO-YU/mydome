package com.blog.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ArticleDTO {

    private Long id;
    private String title;
    private String content;
    private String summary;
    private String coverImage;
    private Long categoryId;
    private String categoryName;
    private Long authorId;
    private String authorNickname;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private String status;
    private Boolean isTop;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
