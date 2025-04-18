package com.example.webchat.service.impl;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    public Optional<Chat> updateChat(String chatName);
    public Optional<Chat> updateChat(Chat chat);
    public List<Message> getChatMessages(String chatName);
    public Optional<Message> addChatMessage(MessageChatDTO messageChatDTO);
    public Optional<Chat> getChatByName(String chatName);
}
