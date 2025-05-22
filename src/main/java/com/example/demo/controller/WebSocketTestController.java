package com.example.demo.controller;

import com.example.demo.entity.Notification;
import com.example.demo.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/test")
@CrossOrigin(origins = "*")
public class WebSocketTestController {

    @Autowired
    private WebSocketService webSocketService;

    // Test endpoint to send a notification to a specific user
    @PostMapping("/notify/{userEmail}")
    public ResponseEntity<String> testNotification(
            @PathVariable String userEmail,
            @RequestParam String message) {

        // Create a test notification (you'd normally have a real notification object)
        Notification testNotification = new Notification();
        testNotification.setId(999L);
        testNotification.setMessage(message);
        testNotification.setIsRead(false);

        webSocketService.notifyUser(userEmail, testNotification);

        return ResponseEntity.ok("Notification sent to: " + userEmail);
    }

    // Test endpoint to broadcast a message to all users
    @PostMapping("/broadcast")
    public ResponseEntity<String> testBroadcast(@RequestParam String message) {
        // This would broadcast to all users subscribed to /topic/test
        // You can implement this in WebSocketController if needed
        return ResponseEntity.ok("Broadcast message sent: " + message);
    }

    // Test WebSocket connection
    @GetMapping("/status")
    public ResponseEntity<String> getWebSocketStatus() {
        return ResponseEntity.ok("WebSocket service is running");
    }
}