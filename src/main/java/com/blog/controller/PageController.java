package com.blog.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/article/{id}")
    public String article(@PathVariable Long id) {
        return "article";
    }

    @GetMapping("/categories")
    public String categories() {
        return "categories";
    }

    @GetMapping("/about")
    public String about() {
        return "about";
    }

    @GetMapping("/write")
    public String write() {
        return "write";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    // ==================== Admin Pages ====================
    
    @GetMapping("/admin/login")
    public String adminLogin() {
        return "admin/login";
    }

    @GetMapping("/admin")
    public String admin() {
        return "admin/index";
    }

    @GetMapping("/admin/users")
    public String adminUsers() {
        return "admin/users";
    }

    @GetMapping("/admin/articles")
    public String adminArticles() {
        return "admin/articles";
    }

    @GetMapping("/admin/categories")
    public String adminCategories() {
        return "admin/categories";
    }

    @GetMapping("/admin/comments")
    public String adminComments() {
        return "admin/comments";
    }

    @GetMapping("/admin/settings")
    public String adminSettings() {
        return "admin/settings";
    }

    @GetMapping("/admin/logs")
    public String adminLogs() {
        return "admin/logs";
    }
}
