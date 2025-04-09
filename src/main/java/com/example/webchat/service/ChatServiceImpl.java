package com.example.webchat.service;

import com.example.webchat.dto.MessageChatDTO;
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

    public List<Message> getChatMessages(String chatName) {
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
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
            Chat newChat = saveChat(messageChatDTO.getChatName()).get();
            messageToSave = messageService.saveMessage(newChat, message);
        }

        return messageToSave;
    }
}
