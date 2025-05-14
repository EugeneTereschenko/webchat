package com.example.webchat.service;

import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.repository.MessageRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class SearchService {

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public List<Chat> searchChats(String keyword) {
        return chatRepository.findByChatNameContainingIgnoreCase(keyword);
    }

    public List<Message> searchMessages(String keyword) {
        List<Message> messages = messageRepository.findByMessageContainingIgnoreCase(keyword);
        log.info(" Found {} messages with keyword: {}", messages.size(), keyword);

        if (messages.isEmpty()) {
            // If no messages found, you can return an empty list or handle it as needed
            return List.of();
        }
        return messages;
    }
}
