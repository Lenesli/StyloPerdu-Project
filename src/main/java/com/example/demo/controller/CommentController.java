package com.example.demo.controller;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired(required = false)
    private SimpMessagingTemplate messagingTemplate;

    // For testing - simple endpoint to verify the controller works
    @GetMapping
    public ResponseEntity<Map<String, Object>> test() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "Comments API is working!");
        response.put("timestamp", LocalDateTime.now());
        response.put("endpoints", new String[]{
            "GET /api/comments - This endpoint",
            "POST /api/comments/items/{itemId} - Add comment to item"
        });
        return ResponseEntity.ok(response);
    }

    @PostMapping("/items/{itemId}")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long itemId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        
        try {
            // Create a mock response for testing
            CommentResponse response = new CommentResponse();
            response.setId(System.currentTimeMillis());
            response.setContent(request.getContent());
            response.setItemId(itemId);
            response.setCreatedAt(LocalDateTime.now());
            response.setUpdatedAt(LocalDateTime.now());
            
            // Set user info if authentication is available
            if (authentication != null && authentication.isAuthenticated()) {
                response.setUserEmail(authentication.getName());
                response.setUserName("Authenticated User");
            } else {
                response.setUserEmail("test@example.com");
                response.setUserName("Test User");
            }
            
            // Send WebSocket notification
            if (messagingTemplate != null) {
                try {
                    messagingTemplate.convertAndSend("/topic/items/" + itemId + "/comments", response);
                } catch (Exception e) {
                    System.err.println("Failed to send WebSocket message: " + e.getMessage());
                }
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("Error processing comment: " + e.getMessage());
            e.printStackTrace();
            
            // Return error response
            CommentResponse errorResponse = new CommentResponse();
            errorResponse.setContent("Error: " + e.getMessage());
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
    
    // Alternative endpoint format that some frontends might expect
    @PostMapping("/item/{itemId}")
    public ResponseEntity<CommentResponse> addCommentAlt(
            @PathVariable Long itemId,
            @RequestBody CommentRequest request,
            Authentication authentication) {
        return addComment(itemId, request, authentication);
    }
}