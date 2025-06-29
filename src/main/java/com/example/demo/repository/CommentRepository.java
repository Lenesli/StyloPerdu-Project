package com.example.demo.repository;

import com.example.demo.entity.Comment;
import com.example.demo.entity.LostItem;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByLostItemOrderByCreatedAtDesc(LostItem lostItem);

    List<Comment> findByUserOrderByCreatedAtDesc(User user);

    Long countByLostItem(LostItem lostItem);
}
