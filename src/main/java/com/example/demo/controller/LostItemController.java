package com.example.demo.controller;

import com.example.demo.dto.LostItemRequest;
import com.example.demo.dto.LostItemResponse;
import com.example.demo.user.User;
import com.example.demo.service.LostItemService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@CrossOrigin(origins = "*")
public class LostItemController {

    @Autowired
    private LostItemService lostItemService;

    @PostMapping
    public ResponseEntity<LostItemResponse> createLostItem(
            @Valid @RequestBody LostItemRequest request,
            @AuthenticationPrincipal User user) {
        LostItemResponse response = lostItemService.createLostItem(request, user);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LostItemResponse> updateLostItem(
            @PathVariable Long id,
            @Valid @RequestBody LostItemRequest request,
            @AuthenticationPrincipal User user) {
        LostItemResponse response = lostItemService.updateLostItem(id, request, user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLostItem(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        lostItemService.deleteLostItem(id, user);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<LostItemResponse> getLostItem(@PathVariable Long id) {
        LostItemResponse response = lostItemService.getLostItemById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/lost")
    public ResponseEntity<List<LostItemResponse>> getAllLostItems() {
        List<LostItemResponse> items = lostItemService.getAllLostItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/found")
    public ResponseEntity<List<LostItemResponse>> getAllFoundItems() {
        List<LostItemResponse> items = lostItemService.getAllFoundItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<LostItemResponse>> getUserItems(
            @AuthenticationPrincipal User user) {
        List<LostItemResponse> items = lostItemService.getUserItems(user);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/search")
    public ResponseEntity<List<LostItemResponse>> searchItems(
            @RequestParam String keyword) {
        List<LostItemResponse> items = lostItemService.searchItems(keyword);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<LostItemResponse>> getItemsByCategory(
            @PathVariable String category) {
        List<LostItemResponse> items = lostItemService.getItemsByCategory(category);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/location/{location}")
    public ResponseEntity<List<LostItemResponse>> getItemsByLocation(
            @PathVariable String location) {
        List<LostItemResponse> items = lostItemService.getItemsByLocation(location);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/date-range")
    public ResponseEntity<List<LostItemResponse>> getItemsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<LostItemResponse> items = lostItemService.getItemsByDateRange(startDate, endDate);
        return ResponseEntity.ok(items);
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<LostItemResponse> markAsResolved(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        LostItemResponse response = lostItemService.markAsResolved(id, user);
        return ResponseEntity.ok(response);
    }
}
