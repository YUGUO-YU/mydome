-- =============================================
-- Personal Blog Database Schema
-- =============================================

-- Create database
CREATE DATABASE IF NOT EXISTS blog DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE blog;

-- =============================================
-- User Table
-- =============================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    avatar VARCHAR(255),
    role VARCHAR(20) DEFAULT 'USER',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Category Table
-- =============================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (name)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Article Table
-- =============================================
DROP TABLE IF EXISTS article;
CREATE TABLE article (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content LONGTEXT,
    summary VARCHAR(500),
    cover_image VARCHAR(255),
    category_id BIGINT,
    author_id BIGINT,
    view_count INT DEFAULT 0,
    like_count INT DEFAULT 0,
    comment_count INT DEFAULT 0,
    status VARCHAR(20) DEFAULT 'DRAFT',
    is_top TINYINT DEFAULT 0,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_category (category_id),
    INDEX idx_author (author_id),
    INDEX idx_status (status),
    INDEX idx_created_at (created_at),
    INDEX idx_view_count (view_count),
    INDEX idx_like_count (like_count),
    FOREIGN KEY (category_id) REFERENCES category(id) ON DELETE SET NULL,
    FOREIGN KEY (author_id) REFERENCES user(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Comment Table
-- =============================================
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT,
    user_id BIGINT,
    parent_id BIGINT,
    content VARCHAR(1000) NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_article (article_id),
    INDEX idx_user (user_id),
    INDEX idx_parent (parent_id),
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Article Like Table
-- =============================================
DROP TABLE IF EXISTS article_like;
CREATE TABLE article_like (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    article_id BIGINT,
    user_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_article_user (article_id, user_id),
    INDEX idx_article (article_id),
    INDEX idx_user (user_id),
    FOREIGN KEY (article_id) REFERENCES article(id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES user(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- =============================================
-- Insert Default Data
-- =============================================

-- Insert default admin user (password: admin123)
INSERT INTO user (username, password, nickname, email, role) VALUES 
('admin', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', '博主', 'admin@blog.com', 'ADMIN');

-- Insert sample categories
INSERT INTO category (name, description) VALUES 
('技术', '技术相关文章'),
('生活', '生活随笔'),
('读书', '读书笔记'),
('旅行', '旅行见闻');

-- Insert sample articles
INSERT INTO article (title, content, summary, category_id, author_id, status, view_count, like_count, comment_count) VALUES 
('欢迎来到我的博客', '# 欢迎\n\n这是我的第一篇博客文章。\n\n欢迎大家来访！', '欢迎来到我的博客', 2, 1, 'PUBLISHED', 100, 10, 5),
('Spring Boot 入门教程', '# Spring Boot 入门\n\n本文介绍 Spring Boot 的基本使用方法...', 'Spring Boot 入门教程', 1, 1, 'PUBLISHED', 50, 5, 2),
('读书笔记：活着', '# 读《活着》有感\n\n今天读完了余华的《活着》...', '读《活着》有感', 3, 1, 'PUBLISHED', 30, 3, 1);

-- Insert sample comments
INSERT INTO comment (article_id, user_id, content) VALUES 
(1, 1, '欢迎欢迎！'),
(2, 1, '写得不错，收藏了');
