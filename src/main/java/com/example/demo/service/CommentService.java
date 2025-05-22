package com.example.demo.service;

import com.example.demo.dto.CommentRequest;
import com.example.demo.dto.CommentResponse;
import com.example.demo.entity.Comment;
import com.example.demo.entity.LostItem;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LostItemRepository;
import com.example.demo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired(required = false) // Made optional in case WebSocket is not configured yet
    private WebSocketService webSocketService;

    @Transactional
    public CommentResponse createComment(Long itemId, CommentRequest request, User user) {
        LostItem item = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        Comment comment = new Comment(request.getContent(), user, item);
        comment = commentRepository.save(comment);

        CommentResponse response = mapToResponse(comment);

        // Send notification to item owner if they're not the commenter
        if (!item.getUser().getId().equals(user.getId())) {
            String message = user.getUsername() + " commented on your " +
                    item.getType().toString().toLowerCase() + " item: " + item.getTitle();

            // Create notification using the NotificationService
            notificationService.createNotification(message, item.getUser(), item);
        }

        // Broadcast new comment to all users viewing this item (if WebSocket is available)
        if (webSocketService != null) {
            webSocketService.broadcastNewComment(itemId, response);
        }

        return response;
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentRequest request, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own comments");
        }

        comment.setContent(request.getContent());
        comment = commentRepository.save(comment);

        CommentResponse response = mapToResponse(comment);

        // Broadcast comment update (if WebSocket is available)
        if (webSocketService != null) {
            webSocketService.broadcastNewComment(comment.getLostItem().getId(), response);
        }

        return response;
    }

    @Transactional
    public void deleteComment(Long id, User user) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own comments");
        }

        commentRepository.delete(comment);

        // Note: You might want to broadcast comment deletion here
        // For now, clients will need to refresh to see deleted comments
    }

    public List<CommentResponse> getCommentsByItem(Long itemId) {
        LostItem item = lostItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        return commentRepository.findByLostItemOrderByCreatedAtDesc(item)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<CommentResponse> getUserComments(User user) {
        return commentRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private CommentResponse mapToResponse(Comment comment) {
        CommentResponse response = new CommentResponse();
        response.setId(comment.getId());
        response.setContent(comment.getContent());
        response.setCreatedAt(comment.getCreatedAt());
        response.setUpdatedAt(comment.getUpdatedAt());
        response.setUserName(comment.getUser().getUsername());
        response.setUserEmail(comment.getUser().getEmail());
        response.setItemId(comment.getLostItem().getId());
        return response;
    }
}