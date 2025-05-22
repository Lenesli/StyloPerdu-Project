package com.example.demo.repository;

import com.example.demo.token.Token;
import com.example.demo.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND t.tokenType = 'REFRESH' AND (t.expired = false AND t.revoked = false)")
    List<Token> findAllValidRefreshTokensByUser(Integer userId);

    @Query("SELECT t FROM Token t WHERE t.user.id = :userId AND (t.expired = false AND t.revoked = false)")
    List<Token> findAllValidTokensByUser(Integer userId);

    Optional<Token> findByToken(String token);
}