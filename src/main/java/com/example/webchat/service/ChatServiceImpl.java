package com.example.webchat.service;

import com.example.webchat.dto.MessageDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.service.impl.ChatService;
import com.example.webchat.service.impl.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ChatServiceImpl implements ChatService  {

    private final ChatRepository chatRepository;
    private final MessageService messageService;

    public Optional<Chat> saveChat(String chatName) {
        Chat chat = new Chat();
        chat.setChatName(chatName);
        chatRepository.save(chat);
        return Optional.of(chat);
    }

    public List<Message> getChatMessages(String chatName) {
        Chat chat = chatRepository.findByChatName(chatName);
        return messageService.getMessagesByChatId(chat.getId());
    }

    public void addChatMessage(String chatName, MessageDTO messageDTO) {
        Message message = new Message();
        message.setUser(messageDTO.getUser());
        message.setMessage(messageDTO.getMessage());
        Chat chat = Optional.ofNullable(chatRepository.findByChatName(chatName))
                .orElseGet(() -> {
                    log.error("Chat not found: " + chatName);
                    Chat newChat = saveChat(chatName).get();
                    messageService.saveMessage(newChat, message);
                    return null;
                });

        if (chat != null) {
            messageService.saveMessage(chat, message);
        }
    }
}
