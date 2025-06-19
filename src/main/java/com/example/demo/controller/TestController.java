package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class TestController {

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "API is working!");
        response.put("timestamp", LocalDateTime.now());
        response.put("server", "Spring Boot Test Server");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "healthy");
        response.put("timestamp", LocalDateTime.now());
        response.put("uptime", "Server is running");
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/comment/{itemId}")
    public ResponseEntity<Map<String, Object>> testComment(
            @PathVariable Long itemId,
            @RequestBody Map<String, String> request) {
        
        // Create response
        Map<String, Object> response = new HashMap<>();
        response.put("id", System.currentTimeMillis());
        response.put("content", request.get("content"));
        response.put("itemId", itemId);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "Test comment received successfully");
        response.put("user", "Test User");
        
        // Send WebSocket notification if available
        if (messagingTemplate != null) {
            try {
                messagingTemplate.convertAndSend("/topic/items/" + itemId + "/comments", response);
                response.put("websocket", "Message sent to WebSocket subscribers");
            } catch (Exception e) {
                response.put("websocket", "WebSocket not available: " + e.getMessage());
            }
        } else {
            response.put("websocket", "SimpMessagingTemplate not available");
        }
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/message")
    public ResponseEntity<Map<String, Object>> testMessage(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("received", request);
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "Message received successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cors")
    public ResponseEntity<Map<String, Object>> testCors() {
        Map<String, Object> response = new HashMap<>();
        response.put("cors", "working");
        response.put("origin", "Any origin allowed");
        response.put("methods", "All methods allowed");
        response.put("headers", "All headers allowed");
        return ResponseEntity.ok(response);
    }
}