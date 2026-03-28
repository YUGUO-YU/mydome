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
}
