package com.example.webchat.service.impl;

import com.example.webchat.dto.ChatDTO;
import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageResponseDTO;
import com.example.webchat.dto.UserChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;

import java.util.List;
import java.util.Optional;

public interface ChatService {
    Optional<ChatDTO> createOrCheckChat(String chatName);
    Optional<Chat> updateChat(Chat chat);
    List<MessageResponseDTO> getNewChatMessages(String chatName, String token);
    List<MessageResponseDTO> getChatMessages(String chatName);
    List<MessageResponseDTO> getOldChatMessages(String chatName, String token);
    Optional<MessageChatDTO> addChatMessage(MessageChatDTO messageChatDTO);
    Optional<Chat> getChatByName(String chatName);
    List<UserChatDTO> getUsersForChat(String chatName);
    Boolean checkNewMessages(String chatName, String token);
    Optional<Chat> addUserToChat(String chatName, String userName);

}
