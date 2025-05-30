package com.example.webchat.repository;

import com.example.webchat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Optional<Chat> findByChatName(String chatName);
    List<Chat> findByChatNameContainingIgnoreCase(String keyword);
}