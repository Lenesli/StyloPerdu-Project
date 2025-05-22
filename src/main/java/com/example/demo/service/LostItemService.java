package com.example.demo.service;

import com.example.demo.dto.LostItemRequest;
import com.example.demo.dto.LostItemResponse;
import com.example.demo.entity.ItemStatus;
import com.example.demo.entity.ItemType;
import com.example.demo.entity.LostItem;
import com.example.demo.repository.CommentRepository;
import com.example.demo.repository.LostItemRepository;
import com.example.demo.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LostItemService {

    @Autowired
    private LostItemRepository lostItemRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private WebSocketService webSocketService;

    @Transactional
    public LostItemResponse createLostItem(LostItemRequest request, User user) {
        LostItem item = new LostItem(
                request.getTitle(),
                request.getDescription(),
                request.getLocation(),
                request.getDate(),
                request.getCategory(),
                request.getType(),
                user
        );

        if (request.getImage() != null) {
            item.setImage(request.getImage());
        }

        item = lostItemRepository.save(item);
        LostItemResponse response = mapToResponse(item);

        // Broadcast new item to all users for real-time feed
        webSocketService.broadcastNewItem(response);

        return response;
    }

    @Transactional
    public LostItemResponse updateLostItem(Long id, LostItemRequest request, User user) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only update your own items");
        }

        item.setTitle(request.getTitle());
        item.setDescription(request.getDescription());
        item.setLocation(request.getLocation());
        item.setDate(request.getDate());
        item.setCategory(request.getCategory());
        item.setType(request.getType());
        if (request.getImage() != null) {
            item.setImage(request.getImage());
        }

        item = lostItemRepository.save(item);
        LostItemResponse response = mapToResponse(item);

        // Broadcast item update to all users
        webSocketService.broadcastItemUpdate(response);

        return response;
    }

    @Transactional
    public void deleteLostItem(Long id, User user) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only delete your own items");
        }

        lostItemRepository.delete(item);

        // Broadcast item deletion to all users
        webSocketService.broadcastItemDeletion(id);
    }

    public LostItemResponse getLostItemById(Long id) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));
        return mapToResponse(item);
    }

    public List<LostItemResponse> getAllLostItems() {
        return lostItemRepository.findByTypeAndStatus(ItemType.LOST, ItemStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getAllFoundItems() {
        return lostItemRepository.findByTypeAndStatus(ItemType.FOUND, ItemStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getUserItems(User user) {
        return lostItemRepository.findByUser(user)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> searchItems(String keyword) {
        return lostItemRepository.searchByKeyword(keyword, ItemStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getItemsByCategory(String category) {
        return lostItemRepository.findByCategoryContainingIgnoreCaseAndStatus(category, ItemStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getItemsByLocation(String location) {
        return lostItemRepository.findByLocationContainingIgnoreCaseAndStatus(location, ItemStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public List<LostItemResponse> getItemsByDateRange(LocalDate startDate, LocalDate endDate) {
        return lostItemRepository.findByDateBetweenAndStatus(startDate, endDate, ItemStatus.ACTIVE)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public LostItemResponse markAsResolved(Long id, User user) {
        LostItem item = lostItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item not found"));

        if (!item.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("You can only resolve your own items");
        }

        item.setStatus(ItemStatus.RESOLVED);
        item = lostItemRepository.save(item);
        LostItemResponse response = mapToResponse(item);

        // Broadcast item update to all users
        webSocketService.broadcastItemUpdate(response);

        return response;
    }

    private LostItemResponse mapToResponse(LostItem item) {
        LostItemResponse response = new LostItemResponse();
        response.setId(item.getId());
        response.setTitle(item.getTitle());
        response.setDescription(item.getDescription());
        response.setLocation(item.getLocation());
        response.setDate(item.getDate());
        response.setCategory(item.getCategory());
        response.setImage(item.getImage());
        response.setType(item.getType());
        response.setStatus(item.getStatus());
        response.setCreatedAt(item.getCreatedAt());
        response.setUpdatedAt(item.getUpdatedAt());
        response.setUserName(item.getUser().getUsername());
        response.setUserEmail(item.getUser().getEmail());

        // Get comment count
        Long commentCount = commentRepository.countByLostItem(item);
        response.setCommentCount(commentCount);

        return response;
    }
}