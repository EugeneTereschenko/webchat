package com.example.webchat.service;

import com.example.webchat.dto.ChatDTO;
import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.repository.MessageRepository;
import lombok.AllArgsConstructor;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Slf4j
@Service
@Data
public class SearchService {

    @Value("${app.search.limit:10}")
    private Long limit;

    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    public SearchService(ChatRepository chatRepository, MessageRepository messageRepository) {
        this.chatRepository = chatRepository;
        this.messageRepository = messageRepository;
    }

    public List<MessageChatDTO> searchUsers(String keyword, String page) {
        List<Message> messages = messageRepository.findByUserContainingIgnoreCase(keyword);
        log.info(" Found {} messages with keyword: {}", messages.size(), keyword);

        if (messages.isEmpty()) {
            // If no messages found, you can return an empty list or handle it as needed
            return List.of();
        }

        int skip = getSkip(page);
        // Convert Message to MessageChatDTO
        List<MessageChatDTO> messageChatDTOs = messages.stream()
                .skip(skip)
                .limit(limit)
                .map(message -> new MessageChatDTO(
                        message.getId(),
                        message.getUser(),
                        "",
                        message.getChat().getChatName()
                ))
                .toList();
        return messageChatDTOs;
    }

    public List<ChatDTO> searchChats(String keyword, String page) {

        List<Chat> chats = chatRepository.findByChatNameContainingIgnoreCase(keyword);
        log.info(" Found {} chats with keyword: {}", chats.size(), keyword);

        if (chats.isEmpty()) {
            return List.of();
        }
        int skip = getSkip(page);
        List<ChatDTO> chatDTOs = chats.stream()
                .skip(skip)
                .limit(limit)
                .map(chat -> new ChatDTO(
                        chat.getId(),
                        chat.getChatName()
                ))
                .toList();

        return chatDTOs;
    }

    public List<MessageChatDTO> searchMessages(String keyword, String page) {
        List<Message> messages = messageRepository.findByMessageContainingIgnoreCase(keyword);
        log.info(" Found {} messages with keyword: {}", messages.size(), keyword);

        if (messages.isEmpty()) {
            // If no messages found, you can return an empty list or handle it as needed
            return List.of();
        }


        // Convert Message to MessageChatDTO
        int skip = getSkip(page);
        List<MessageChatDTO> messageChatDTOs = messages.stream()
                .skip(skip)
                .limit(limit)
                .map(message -> new MessageChatDTO(
                        message.getId(),
                        message.getUser(),
                        message.getMessage(),
                        message.getChat().getChatName()
                ))
                .toList();

        return messageChatDTOs;
    }

    private int getSkip(String page) {
        int skip = (int) (limit * (Long.parseLong(page) - 1));
        return skip;
    }

}
