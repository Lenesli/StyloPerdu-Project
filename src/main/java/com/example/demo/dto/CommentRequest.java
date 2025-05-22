package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

public class CommentRequest {

    @NotBlank(message = "Content is required")
    private String content;

    // Constructors
    public CommentRequest() {}

    public CommentRequest(String content) {
        this.content = content;
    }

    // Getters and Setters
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
