package com.example.webchat.service;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageResponseDTO;
import com.example.webchat.dto.UserChatDTO;
import com.example.webchat.model.*;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.repository.ChatUsersRepository;
import com.example.webchat.repository.MessageRepository;
import com.example.webchat.service.impl.ActivityService;
import com.example.webchat.service.impl.ChatService;
import com.example.webchat.service.impl.MessageService;
import com.example.webchat.util.DateTimeConverter;
import com.example.webchat.util.TimeAgoFormatter;
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
    private final ImageService imageService;
    private final ChatUsersRepository chatUsersRepository;
    private final ProfileServiceImpl profileService;

    public Optional<Chat> createOrCheckChat(String chatName) {
        User user = userService.getAuthenticatedUser();
        Optional<Chat> existingChat = chatRepository.findByChatName(chatName);
        if (existingChat.isPresent()) {
            log.debug("Chat already exists: " + chatName);
            activityService.addActivity("Open Chat", user.getUserID(), new Date());
            updateUserInChat(user, existingChat);
            return existingChat;
        } else {
            log.debug("Creating new chat: " + chatName);
            activityService.addActivity("Create Chat", user.getUserID(), new Date());
            Chat chat = new Chat();
            chat.setChatName(chatName);
            chatRepository.save(chat);

            addUserToChat(chat, user);

            return Optional.of(chat);
        }
    }

    private void updateUserInChat(User user, Optional<Chat> existingChat) {
        chatUsersRepository.findByUserIdAndChatId(user.getUserID(), existingChat.get().getId())
                .ifPresentOrElse(
                        chatUsers -> {
                            log.debug("User already in chat: " + user.getUsername() + " in chat: " + existingChat.get().getChatName());
                            chatUsers.setTime(new Date());
                            chatUsersRepository.save(chatUsers);
                        },
                        () -> {
                            log.debug("Adding user to existing chat: " + user.getUsername() + " in chat: " + existingChat.get().getChatName());
                            addUserToChat(existingChat.get(), user);
                        }
                );
    }

    private void addUserToChat(Chat chat, User user) {
        ChatUsers existingChatUsers = chatUsersRepository.findByUserIdAndChatId(user.getUserID(), chat.getId())
                .orElse(null);

        if (existingChatUsers != null) {
            log.debug("User already exists in chat: " + user.getUsername() + " in chat: " + chat.getChatName());
            existingChatUsers.setTime(new Date());
            chatUsersRepository.save(existingChatUsers);
            return;
        }

        ChatUsers chatUsers = new ChatUsers.Builder()
                .chatId(chat.getId())
                .userId(user.getUserID())
                .time(new Date())
                .build();

        chatUsersRepository.save(chatUsers);
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
        User user = userService.getAuthenticatedUser();
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

                    byte[] imageData = new byte[0]; // Placeholder for avatar URL
                    Optional<Image> optionalImage = imageService.getImageByUserName(message.getUser());
                    if (optionalImage.isPresent()) {
                        imageData = optionalImage.get().getData();
                    } else {
                        log.warn("No image found for user ID: " + user.getUserID());
                        imageData = new byte[0]; // Provide a default or empty byte array
                    }

                    MessageResponseDTO messageResponseDTO = new MessageResponseDTO.Builder()
                            .id(message.getId())
                            .username(message.getUser())
                            .message(message.getMessage())
                            .time(TimeAgoFormatter.timeAgo(DateTimeConverter.toLocalDateTime(message.getTime())))
                            .isRead(message.getIsRead())
                            .avatar(imageData)
                            .build();

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

                    byte[] imageData = new byte[0]; // Placeholder for avatar URL
                    Optional<Image> optionalImage = imageService.getImageByUserName(message.getUser());
                    if (optionalImage.isPresent()) {
                        imageData = optionalImage.get().getData();
                    } else {
                        log.warn("No image found for user ID: " + user.getUserID());
                        imageData = new byte[0]; // Provide a default or empty byte array
                    }

                    MessageResponseDTO messageResponseDTO = new MessageResponseDTO.Builder()
                            .id(message.getId())
                            .username(message.getUser())
                            .message(message.getMessage())
                            .time(TimeAgoFormatter.timeAgo(DateTimeConverter.toLocalDateTime(message.getTime())))
                            .isRead(message.getIsRead())
                            .avatar(imageData)
                            .build();
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
    public List<MessageResponseDTO> getOldChatMessages(String chatName, String token) {
        String userToken = splitToken(token);
        User user = userService.getAuthenticatedUser();
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()) {
            log.debug("Chat not found: " + chatName);
            return List.of();
        }
        log.debug("get old chat messages:");
        log.debug("getOldChatMessages chatName: " + chatName);
        log.debug("getOldChatMessages chatId: " + chat.get().getId());


        List<MessageResponseDTO> messages = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersToken() == null) {
                        message.setUsersToken(new ArrayList<>()); // Initialize if null
                    }
                    return message.getUsersToken().contains(userToken);
                })
                .map(message -> {

                    byte[] imageData = new byte[0]; // Placeholder for avatar URL
                    Optional<Image> optionalImage = imageService.getImageByUserName(message.getUser());
                    if (optionalImage.isPresent()) {
                        imageData = optionalImage.get().getData();
                    } else {
                        log.warn("No image found for user ID: " + user.getUserID());
                        imageData = new byte[0]; // Provide a default or empty byte array
                    }

                    MessageResponseDTO messageResponseDTO = new MessageResponseDTO.Builder()
                            .id(message.getId())
                            .message(message.getMessage())
                            .username(message.getUser())
                            .time(TimeAgoFormatter.timeAgo(DateTimeConverter.toLocalDateTime(message.getTime())))
                            .isRead(message.getIsRead())
                            .avatar(imageData)
                            .build();

                    return messageResponseDTO;
                })
                .toList();
        log.debug("messages found");
        log.debug("getOldChatMessages messages: " + messages.size());

        return messages;
    }

    public Optional<MessageChatDTO> addChatMessage(MessageChatDTO messageChatDTO) {
        User user = userService.getAuthenticatedUser();
        Message message = new Message.Builder()
              //  .id(messageChatDTO.getId())
                .user(user.getUsername())
                .message(messageChatDTO.getMessage())
                .usersToken(new ArrayList<>())
                .time(new Date())
                .build();

        Optional<Chat> chat = chatRepository.findByChatName(messageChatDTO.getChatName());

        Optional<Message> messageToSave = null;
        if (chat.isPresent()) {
            log.debug("Chat found: " + messageChatDTO.getChatName());
            messageToSave = messageService.saveMessage(chat.get(), message);
        } else {
            log.debug("Chat not found: " + messageChatDTO.getChatName());
            Chat newChat = createOrCheckChat(messageChatDTO.getChatName()).get();
            messageToSave = messageService.saveMessage(newChat, message);
        }
        MessageChatDTO messageChatDTOToSave = new MessageChatDTO.Builder()
                .id(messageToSave.get().getId())
                .user(messageToSave.get().getUser())
                .message(messageToSave.get().getMessage())
                .chatName(messageToSave.get().getChat().getChatName())
                .build();

        return Optional.of(messageChatDTOToSave);
    }

    public Boolean checkNewMessages(String chatName, String token) {
        String userToken = splitToken(token);
        Optional<Chat> chat = chatRepository.findByChatName(chatName);
        if (chat.isEmpty()) {
            log.debug("Chat not found: " + chatName);
            return false;
        }
        log.debug("check new messages:");
        List<Message> messages = messageService.getMessagesByChatId(chat.get().getId())
                .stream()
                .filter(message -> {
                    if (message.getUsersToken() == null) {
                        message.setUsersToken(new ArrayList<>()); // Initialize if null
                    }
                    return !message.getUsersToken().contains(userToken);
                })
                .toList();
        log.debug("messages found");
        log.debug("checkNewMessages messages: " + messages.size());
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
                addUserToChat(chat.get(), user);
                log.debug("User added to chat: " + user.getUsername() + " to chat: " + chatName);
            } else {
                //updateUserInChat(user, chat);
                log.debug("User already in chat: " + user.getUsername());
            }
            updateChat(chat.get());
            return chat.get().getUsers().stream().map(username -> {

                byte[] imageData = new byte[0]; // Placeholder for avatar URL
                Optional<Image> optionalImage = imageService.getImageByUserName(username);
                if (optionalImage.isPresent()) {
                    imageData = optionalImage.get().getData();
                } else {
                    log.warn("No image found for user ID: " + user.getUserID());
                    imageData = new byte[0]; // Provide a default or empty byte array
                }

                Long unreadCount = messageService.countUnreadMessagesByUser(username);
                Optional<Profile> profile = profileService.getProfileByUserName(username);
                UserChatDTO userChatDTO = new UserChatDTO.Builder()
                        .userId(String.valueOf(userService.getUserByUsername(username).getUserID()))
                        .username(username)
                        .avatar(imageData)
                        .message(profile.map(Profile::getMessage).orElse("No message"))
                        .unreadCount(String.valueOf(unreadCount))
                        .time(getTime(user, chat))
                        .build();
                return userChatDTO;

            }).toList();

        } else {
            log.debug("Chat not found, returning user: " + user.getUsername());
            return users;
        }
    }

    private String getTime(User user, Optional<Chat> chat) {
        Date date = chatUsersRepository.findTimeByUserIdAndChatId(user.getUserID(), chat.get().getId());
        String timeAgo = TimeAgoFormatter.timeAgo(DateTimeConverter.toLocalDateTime(date));
        return timeAgo;
    }


    public String splitToken(String token) {
        if (token == null || token.isEmpty()) {
            log.debug("Token is null or empty");
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
