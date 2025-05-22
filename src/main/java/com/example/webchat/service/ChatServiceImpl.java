package com.example.webchat.service;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageResponseDTO;
import com.example.webchat.dto.UserChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.service.impl.ActivityService;
import com.example.webchat.service.impl.ChatService;
import com.example.webchat.service.impl.MessageService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ChatServiceImpl implements ChatService  {

    private final ChatRepository chatRepository;
    private final MessageService messageService;
    private final UserService userService;
    private final ActivityService activityService;

    public Optional<Chat> createOrCheckChat(String chatName) {
        User user = userService.getAuthenticatedUser();
        Optional<Chat> existingChat = chatRepository.findByChatName(chatName);
        if (existingChat.isPresent()) {
            log.debug("Chat already exists: " + chatName);
            activityService.addActivity("Open Chat", user.getUserID(), new Date());
            return existingChat;
        } else {
            log.debug("Creating new chat: " + chatName);
            activityService.addActivity("Create Chat", user.getUserID(), new Date());
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
            return null;
        }
    }

    @Override
    public List<MessageResponseDTO> getNewChatMessages(String chatName, String token){
        String userToken = splitToken(token);
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()) {
            log.info("Chat not found: " + chatName);
            return List.of();
        }
        log.info("get new chat messages:");
        List<Message> messages = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersToken() == null) {
                        message.setUsersToken(new ArrayList<>()); // Initialize if null
                    }
                    return !message.getUsersToken().contains(userToken);
                })
                .toList();
        log.info("messages found");
        log.info("getNewChatMessages messages: " + messages.size());

        // Mark messages as read by user
        messages.forEach(message -> {
            if (!message.getUsersToken().contains(userToken)) {
                message.getUsersToken().add(userToken);
            }
            messageService.updateMessage(message);
        });

        return messages.stream().map(message -> {
                    MessageResponseDTO messageResponseDTO = new MessageResponseDTO();
                    messageResponseDTO.setUsername(message.getUser());
                    messageResponseDTO.setMessage(message.getMessage());
                    messageResponseDTO.setTime(new Date().toString());
                    messageResponseDTO.setAvatar("https://example.com/avatar.png"); // Placeholder for avatar URL
                    return messageResponseDTO;
                })
                .peek(messageResponseDTO -> {
                    if (messageResponseDTO.getUsername() == null) {
                        messageResponseDTO.setUsername("Unknown");
                    }
                })
                .toList();

    }

    @Override
    public List<MessageResponseDTO> getChatMessages(String chatName) {
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

        return messages.stream().map(message -> {
                    MessageResponseDTO messageResponseDTO = new MessageResponseDTO();
                    messageResponseDTO.setUsername(message.getUser());
                    messageResponseDTO.setMessage(message.getMessage());
                    messageResponseDTO.setTime(new Date().toString());
                    messageResponseDTO.setAvatar("https://example.com/avatar.png"); // Placeholder for avatar URL
                    return messageResponseDTO;
                })
                .peek(messageResponseDTO -> {
                    if (messageResponseDTO.getUsername() == null) {
                        messageResponseDTO.setUsername("Unknown");
                    }
                })
                .toList();

        //return messages;
    }

    @Override
    public List<MessageChatDTO> getOldChatMessages(String chatName, String token) {
        String userToken = splitToken(token);
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()) {
            log.info("Chat not found: " + chatName);
            return List.of();
        }
        log.info("get old chat messages:");
        log.info("getOldChatMessages chatName: " + chatName);
        log.info("getOldChatMessages chatId: " + chat.get().getId());


        List<MessageChatDTO> messages = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersToken() == null) {
                        message.setUsersToken(new ArrayList<>()); // Initialize if null
                    }
                    return message.getUsersToken().contains(userToken);
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
        log.info("getOldChatMessages messages: " + messages.size());

        return messages;
    }

    public Optional<MessageChatDTO> addChatMessage(MessageChatDTO messageChatDTO) {
        User user = userService.getAuthenticatedUser();
        Optional<Message> messageToSave = null;
        MessageChatDTO messageChatDTOToSave = new MessageChatDTO();
        Message message = new Message();
        message.setUser(user.getUsername());
        message.setMessage(messageChatDTO.getMessage());
        Optional<Chat> chat = chatRepository.findByChatName(messageChatDTO.getChatName());

        if (chat.isPresent()) {
            log.info("Chat found: " + messageChatDTO.getChatName());
            messageToSave = messageService.saveMessage(chat.get(), message);
        } else {
            log.info("Chat not found: " + messageChatDTO.getChatName());
            Chat newChat = createOrCheckChat(messageChatDTO.getChatName()).get();
            messageToSave = messageService.saveMessage(newChat, message);
        }


        messageChatDTOToSave.setId(messageToSave.get().getId());
        messageChatDTOToSave.setMessage(messageToSave.get().getMessage());
        messageChatDTOToSave.setUser(messageToSave.get().getUser());
        messageChatDTOToSave.setChatName(messageToSave.get().getChat().getChatName());

        return Optional.of(messageChatDTOToSave);
    }

    public Boolean checkNewMessages(String chatName, String token) {
        String userToken = splitToken(token);
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()) {
            log.info("Chat not found: " + chatName);
            return false;
        }
        log.info("check new messages:");
        List<Message> messages = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersToken() == null) {
                        message.setUsersToken(new ArrayList<>()); // Initialize if null
                    }
                    return !message.getUsersToken().contains(userToken);
                })
                .toList();
        log.info("messages found");
        log.info("checkNewMessages messages: " + messages.size());
        return !messages.isEmpty();
    }

    public Optional<Chat> getChatByName(String chatName) {
        return chatRepository.findByChatName(chatName);
    }

    public List<UserChatDTO> getUsersForChat(String chatName) {
        List<UserChatDTO> users = new ArrayList<>();
        Optional<Chat> chat = getChatByName(chatName);
        User user = userService.getAuthenticatedUser();

        if (chat.isPresent()) {
            if (chat.get().getUsers() == null) {
                chat.get().setUsers(new ArrayList<>()); // Initialize the users list if null
            }

            if (!chat.get().getUsers().contains(user.getUsername())) {
                chat.get().getUsers().add(user.getUsername());
                log.info("User added to chat: " + user.getUsername() + " to chat: " + chatName);
            } else {
                log.info("User already in chat: " + user.getUsername());
            }
            updateChat(chat.get());
            return chat.get().getUsers().stream().map(username -> {
                UserChatDTO userChatDTO = new UserChatDTO();
                userChatDTO.setUsername(username);
                return userChatDTO;
            }).toList();
        } else {
            UserChatDTO userChatDTO = new UserChatDTO();
            userChatDTO.setUsername(user.getUsername());
            users.add(userChatDTO);
            log.info("Chat not found, returning user: " + user.getUsername());
            return users;
        }
    }


    public String splitToken(String token) {
        if (token == null || token.isEmpty()) {
            log.info("Token is null or empty");
            return null;
        }
        String[] tokenParts = token.split(" ")[1].split("\\.");
        if (tokenParts.length == 3) {
            String thirdPart = tokenParts[2];
            System.out.println("Third part of the token: " + thirdPart);
            return thirdPart;
        } else {
            System.out.println("Invalid token format");
            return null;
        }
    }
}
