package com.example.webchat.service;

import com.example.webchat.dto.MessageChatDTO;
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

    public Optional<Chat> updateChat(String chatName) {

        Optional<Chat> existingChat = chatRepository.findByChatName(chatName);
        if (existingChat.isPresent()) {
            log.info("Chat already exists: " + chatName);
            return existingChat;
        } else {
            log.info("Creating new chat: " + chatName);
            Chat chat = new Chat();
            chat.setChatName(chatName);
            chatRepository.save(chat);
            return Optional.of(chat);
        }
    }

    public Optional<Chat> updateChat(Chat chat) {
        Optional<Chat> existingChat = chatRepository.findByChatName(chat.getChatName());
        if (existingChat.isPresent()) {
            log.info("Chat already exists: " + chat.getChatName());
            chatRepository.save(chat);
            return existingChat;
        } else {
            log.info("exists such chat: " + chat.getChatName());
            //chatRepository.save(chat);
            return null;
        }
    }

    public List<Message> getChatMessages(String chatName) {
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()){
            log.info("Chat not found: " + chatName);
            return List.of();
        }
        return messageService.getMessagesByChatId(chat.get().getId());
    }

    public Optional<Message> addChatMessage(MessageChatDTO messageChatDTO) {
        Optional<Message> messageToSave = null;
        Message message = new Message();
        message.setUser(messageChatDTO.getUser());
        message.setMessage(messageChatDTO.getMessage());
        Optional<Chat> chat = chatRepository.findByChatName(messageChatDTO.getChatName());

        if (chat.isPresent()) {
            log.info("Chat found: " + messageChatDTO.getChatName());
            messageToSave = messageService.saveMessage(chat.get(), message);
        } else {
            log.info("Chat not found: " + messageChatDTO.getChatName());
            Chat newChat = updateChat(messageChatDTO.getChatName()).get();
            messageToSave = messageService.saveMessage(newChat, message);
        }

        return messageToSave;
    }

    public Optional<Chat> getChatByName(String chatName) {
        return chatRepository.findByChatName(chatName);
    }
}
