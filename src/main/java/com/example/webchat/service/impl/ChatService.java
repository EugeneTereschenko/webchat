package com.example.webchat.service.impl;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageResponseDTO;
import com.example.webchat.dto.UserChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    public Optional<Chat> createOrCheckChat(String chatName);
    public Optional<Chat> updateChat(Chat chat);
    public List<MessageResponseDTO> getNewChatMessages(String chatName, String token);
    public List<MessageResponseDTO> getChatMessages(String chatName);
    public List<MessageChatDTO> getOldChatMessages(String chatName, String token);
    public Optional<MessageChatDTO> addChatMessage(MessageChatDTO messageChatDTO);
    public Optional<Chat> getChatByName(String chatName);
    public List<UserChatDTO> getUsersForChat(String chatName);
    public Boolean checkNewMessages(String chatName, String token);

}
