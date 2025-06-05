package com.example.webchat.service.impl;

import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;

import java.util.List;
import java.util.Optional;

public interface MessageService {
    Optional<Message> getMessageById(Long id);
    Optional<Message> saveMessage(Chat chat, Message message);
    Optional<Message> updateMessage(Message message);
    List<Message> getMessagesByChatId(Long chatId);
    Long countUnreadMessagesByUser(String user, Long chatId);
}
