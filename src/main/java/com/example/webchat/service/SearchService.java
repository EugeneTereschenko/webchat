package com.example.webchat.service;

import com.example.webchat.dto.ChatDTO;
import com.example.webchat.dto.MessageChatDTO;
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

    public List<ChatDTO> searchChats(String keyword) {

        List<Chat> chats = chatRepository.findByChatNameContainingIgnoreCase(keyword);
        log.info(" Found {} chats with keyword: {}", chats.size(), keyword);

        if (chats.isEmpty()) {
            return List.of();
        }

        List<ChatDTO> chatDTOs = chats.stream()
                .map(chat -> new ChatDTO(
                        chat.getId(),
                        chat.getChatName()
                ))
                .toList();

        return chatDTOs;
    }

    public List<MessageChatDTO> searchMessages(String keyword) {
        List<Message> messages = messageRepository.findByMessageContainingIgnoreCase(keyword);
        log.info(" Found {} messages with keyword: {}", messages.size(), keyword);

        if (messages.isEmpty()) {
            // If no messages found, you can return an empty list or handle it as needed
            return List.of();
        }


        // Convert Message to MessageChatDTO
        List<MessageChatDTO> messageChatDTOs = messages.stream()
                .map(message -> new MessageChatDTO(
                        message.getId(),
                        message.getUser(),
                        message.getMessage(),
                        message.getChat().getChatName()
                ))
                .toList();
        return messageChatDTOs;
    }
}
