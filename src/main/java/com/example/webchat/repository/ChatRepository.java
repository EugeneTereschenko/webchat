package com.example.webchat.repository;

import com.example.webchat.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find chats by user ID or other criteria
}
