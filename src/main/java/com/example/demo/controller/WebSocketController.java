package com.example.demo.controller;

import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.LostItemResponse;
import com.example.demo.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Send notification to specific user
    public void sendNotificationToUser(String userEmail, Notification notification) {
        messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", notification);
    }

    // Send new item to all users (real-time feed)
    public void sendNewItemToAll(LostItemResponse item) {
        messagingTemplate.convertAndSend("/topic/items", item);
    }

    // Send new comment to all users viewing the item
    public void sendNewCommentToItem(Long itemId, CommentResponse comment) {
        messagingTemplate.convertAndSend("/topic/items/" + itemId + "/comments", comment);
    }

    // Send item update to all users
    public void sendItemUpdateToAll(LostItemResponse item) {
        messagingTemplate.convertAndSend("/topic/items/update", item);
    }

    // Send item deletion notification
    public void sendItemDeletionToAll(Long itemId) {
        messagingTemplate.convertAndSend("/topic/items/delete", itemId);
    }

    @MessageMapping("/join")
    @SendTo("/topic/users")
    public String addUser(@Payload String username, SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", username);
        return username + " joined the chat!";
    }

    @MessageMapping("/leave")
    @SendTo("/topic/users")
    public String removeUser(@Payload String username, SimpMessageHeaderAccessor headerAccessor) {
        return username + " left the chat!";
    }
}