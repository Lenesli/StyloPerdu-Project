package com.example.demo.service;

import com.example.demo.controller.WebSocketController;
import com.example.demo.dto.CommentResponse;
import com.example.demo.dto.LostItemResponse;
import com.example.demo.entity.Notification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebSocketService {

    @Autowired
    private WebSocketController webSocketController;

    public void notifyUser(String userEmail, Notification notification) {
        webSocketController.sendNotificationToUser(userEmail, notification);
    }

    public void broadcastNewItem(LostItemResponse item) {
        webSocketController.sendNewItemToAll(item);
    }

    public void broadcastNewComment(Long itemId, CommentResponse comment) {
        webSocketController.sendNewCommentToItem(itemId, comment);
    }

    public void broadcastItemUpdate(LostItemResponse item) {
        webSocketController.sendItemUpdateToAll(item);
    }

    public void broadcastItemDeletion(Long itemId) {
        webSocketController.sendItemDeletionToAll(itemId);
    }
}