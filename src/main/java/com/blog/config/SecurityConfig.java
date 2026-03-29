package com.blog.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().and()
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
                // Public APIs
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/categories/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/comments/**").permitAll()
                
                // Admin APIs - require ADMIN or SUPER_ADMIN role
                .antMatchers("/api/admin/**").hasAnyRole("ADMIN", "SUPER_ADMIN")
                
                // User APIs - require authentication
                .antMatchers("/api/users/**").authenticated()
                .antMatchers("/api/articles/**").authenticated()
                .antMatchers("/api/likes/**").authenticated()
                
                // Frontend pages
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .httpBasic().disable();
        
        return http.build();
    }
}
