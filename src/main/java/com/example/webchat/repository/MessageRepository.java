package com.example.webchat.repository;

import com.example.webchat.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    // Custom query methods can be defined here if needed
    // For example, to find messages by chat ID or other criteria
    List<Message> findByChatId(Long chatId);
    List<Message> findByMessageContainingIgnoreCase(String keyword);
    List<Message> findByUserContainingIgnoreCase(String keyword);

    @Query("SELECT count(c) FROM Message c WHERE c.user = :user AND c.isRead = false")
    Long countUnreadMessageByUser(@Param("user") String user);
}
