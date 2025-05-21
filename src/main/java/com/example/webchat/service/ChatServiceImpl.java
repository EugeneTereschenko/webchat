package com.example.webchat.service;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.repository.MessageRepository;
import com.example.webchat.service.impl.ChatService;
import com.example.webchat.service.impl.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ChatServiceImpl implements ChatService  {

    private final ChatRepository chatRepository;
    private final MessageService messageService;
    private final UserService userService;
    private final MessageRepository messageRepository;

    public Optional<Chat> updateChat(String chatName) {

        Optional<Chat> existingChat = chatRepository.findByChatName(chatName);
        if (existingChat.isPresent()) {
            log.info("Chat already exists: " + chatName);
/*            List<Message> messages = messageService.getMessagesByChatId(existingChat.get().getId())
                    .stream()
                    .filter(message -> message.getUsersRead() != null && message.getUsersRead().contains(user.getUsername()))
                    .toList();
            log.info("Marking messages as unread by user for chat: " + chatName);
            log.info("Messages: " + messages.size());
            messages.forEach(message -> {
                if (message.getUsersRead().contains(user.getUsername())) {
                    message.getUsersRead().remove(user.getUsername());
                    messageService.updateMessage(message);
                }
            });*/
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

    @Override
    public List<Message> getChatMessages(String chatName) {
        User user = userService.getAuthenticatedUser();
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()) {
            log.info("Chat not found: " + chatName);
            return List.of();
        }
        log.info("get chat messages:");
        List<Message> messages = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersRead() == null) {
                        message.setUsersRead(new ArrayList<>()); // Initialize if null
                    }
                    return !message.getUsersRead().contains(user.getUsername());
                })
                .toList();
        log.info("messages found");
        log.info("getChatMessages messages: " + messages.size());
        // Mark messages as read by user
        messages.forEach(message -> {
            if (!message.getUsersRead().contains(user.getUsername())) {
                message.getUsersRead().add(user.getUsername());
                messageService.updateMessage(message);
            }
        });

        return messages;
    }

    @Override
    public List<MessageChatDTO> getOldChatMessages(String chatName) {
        User user = userService.getAuthenticatedUser();
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()) {
            log.info("Chat not found: " + chatName);
            return List.of();
        }
        log.info("get old chat messages:");
        log.info("getOldChatMessages chatName: " + chatName);
        log.info("getOldChatMessages chatId: " + chat.get().getId());

        List<Message> messagesNoNeList = messageRepository.findByChatId(chat.get().getId());

        log.info("messages found without user contains " + messagesNoNeList.size());

/*        List<Message> messagesList = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersRead() == null) {
                        message.setUsersRead(new ArrayList<>()); // Initialize if null
                    }
                    return message.getUsersRead().contains(user.getUsername());
                })
                .toList();

        log.info("messages found with user contains " + messagesList.size());




        List<MessageChatDTO> messages = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersRead() == null) {
                        message.setUsersRead(new ArrayList<>()); // Initialize if null
                    }
                    return message.getUsersRead().contains(user.getUsername());
                })
                .map(message -> {
                    MessageChatDTO messageChatDTO = new MessageChatDTO();
                    messageChatDTO.setId(message.getId());
                    messageChatDTO.setMessage(message.getMessage());
                    messageChatDTO.setUser(message.getUser());
                    return messageChatDTO;
                })
                .toList();
        log.info("messages found");
        log.info("getOldChatMessages messages: " + messages.size());*/

        List<MessageChatDTO> messages = messagesNoNeList.stream()
                .map(message -> {
                    MessageChatDTO messageChatDTO = new MessageChatDTO();
                    messageChatDTO.setId(message.getId());
                    messageChatDTO.setMessage(message.getMessage());
                    messageChatDTO.setUser(message.getUser());
                    return messageChatDTO;
                })
                .toList();
        return messages;
    }

    public Optional<Message> addChatMessage(MessageChatDTO messageChatDTO) {
        User user = userService.getAuthenticatedUser();
        Optional<Message> messageToSave = null;
        Message message = new Message();
        message.setUser(user.getUsername());
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
