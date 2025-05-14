package com.example.webchat.repository;

import com.example.webchat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find messages by chat ID or other criteria
    List<Message> findByChatId(Long chatId);
    List<Message> findByMessageContainingIgnoreCase(String keyword);
}
