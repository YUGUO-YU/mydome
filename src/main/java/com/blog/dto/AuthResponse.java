package com.blog.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private Long userId;
    private String username;
    private String nickname;
    private String token;
    private String role;

    public AuthResponse() {}

    public AuthResponse(Long userId, String username, String nickname, String token, String role) {
        this.userId = userId;
        this.username = username;
        this.nickname = nickname;
        this.token = token;
        this.role = role;
    }
}
