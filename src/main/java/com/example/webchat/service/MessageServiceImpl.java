package com.example.webchat.service;

import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.repository.MessageRepository;
import com.example.webchat.service.impl.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public Optional<Message> saveMessage(Chat chat, Message message) {
        Message messageToSave = new Message.Builder()
                .id(message.getId())
                .user(Optional.ofNullable(message.getUser()).orElse(""))
                .message(Optional.ofNullable(message.getMessage()).orElse(""))
                .time(message.getTime())
                .isRead(message.getIsRead())
                .chat(chat)
                .build();

        messageRepository.save(messageToSave);
        return Optional.of(messageToSave);
    }

    @Override
    public Optional<Message> updateMessage(Message message) {
        return messageRepository.findById(message.getId())
                .map(existingMessage -> {
                    existingMessage.setId(message.getId());
                    existingMessage.setUser(Optional.ofNullable(message.getUser()).orElse(""));
                    existingMessage.setMessage(Optional.ofNullable(message.getMessage()).orElse(""));
                    existingMessage.setTime(Optional.ofNullable(message.getTime()).orElse(new Date()));
                    existingMessage.setIsRead(message.getIsRead());
                    return messageRepository.save(existingMessage);
                });
    }

    @Override
    public List<Message> getMessagesByChatId(Long chatId) {
        return messageRepository.findByChatId(chatId);
    }

    public Boolean isMessageRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        if (message.getIsRead() == null) {
            return false;
        }
        return message.getIsRead();
    }

    public void markMessageAsRead(String messageId) {

        Message message = messageRepository.findById(Long.parseLong(messageId))
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setIsRead(true);
        messageRepository.save(message);
    }
}
