package com.example.demo.repository;

import com.example.demo.entity.LostItem;
import com.example.demo.entity.ItemType;
import com.example.demo.entity.ItemStatus;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LostItemRepository extends JpaRepository<LostItem, Long> {

    List<LostItem> findByTypeAndStatus(ItemType type, ItemStatus status);

    List<LostItem> findByUserAndStatus(User user, ItemStatus status);

    List<LostItem> findByUser(User user);

    List<LostItem> findByCategoryContainingIgnoreCaseAndStatus(String category, ItemStatus status);

    List<LostItem> findByLocationContainingIgnoreCaseAndStatus(String location, ItemStatus status);

    @Query("SELECT l FROM LostItem l WHERE l.status = :status AND " +
            "(LOWER(l.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(l.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(l.category) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(l.location) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<LostItem> searchByKeyword(@Param("keyword") String keyword, @Param("status") ItemStatus status);

    List<LostItem> findByDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, ItemStatus status);

    @Query("SELECT l FROM LostItem l WHERE l.type = :type AND l.status = :status ORDER BY l.createdAt DESC")
    List<LostItem> findRecentItemsByType(@Param("type") ItemType type, @Param("status") ItemStatus status);
}
