package com.example.webchat.controller;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.service.UserService;
import com.example.webchat.service.impl.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Controller
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;


    //@PreAuthorize("userService.isAuthenticated()")
    @GetMapping("/chat")
    public String home(Model model) {

        User user = userService.getAuthenticatedUser();
        if (user == null) {
            System.out.println("User is not authenticated");
            return "redirect:/api/login"; // Redirect to login page if user is not authenticated
        }

        return "index";
    }


    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<String>> getUsersForChat(@RequestParam String chatName) {
        List<String> users = new ArrayList<>();
        Optional<Chat> chat = chatService.getChatByName(chatName);
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
            chatService.updateChat(chat.get());
            return ResponseEntity.ok(chat.get().getUsers());
        } else {
            users.add(user.getUsername());
            log.info("Chat not found, returning user: " + user.getUsername());
            return ResponseEntity.ok(users);
        }
    }


    @GetMapping("/api/user")
    @ResponseBody
    public ResponseEntity<HashMap<String, String>> getUserForChat(@RequestParam String chatName) {
        User user = userService.getAuthenticatedUser();
        log.info("User connected to chat: " + user.getUsername());

        HashMap<String, String> response = new HashMap<>();
        response.put("username", user.getUsername());
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/chat")
    public ResponseEntity<List<Message>> getChatMessages(@RequestParam String chatName) {
        List<Message> messages = chatService.getChatMessages(chatName);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/api/chatAdd")
    public ResponseEntity<Message> addChatMessage(@Valid @RequestBody MessageChatDTO messageChatDTO) {

        System.out.println("Received messageDTO: " + messageChatDTO);

        Optional<Message> savedMessage = chatService.addChatMessage(messageChatDTO);
        return ResponseEntity.ok(savedMessage.get());
    }

    @PostMapping("/api/chatCreate")
    public ResponseEntity<Chat> createChat(@RequestParam String name) {
        Optional<Chat> chat = chatService.updateChat(name);
        return ResponseEntity.ok(chat.get());
    }


}
