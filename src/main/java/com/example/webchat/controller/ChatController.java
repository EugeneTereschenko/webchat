package com.example.webchat.controller;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageResponseDTO;
import com.example.webchat.dto.UserChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.service.UserService;
import com.example.webchat.service.impl.ActivityService;
import com.example.webchat.service.impl.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Controller
public class ChatController {

    private final UserService userService;
    private final ChatService chatService;

    @GetMapping("/api/users")
    @ResponseBody
    public ResponseEntity<List<UserChatDTO>> getUsersForChat(@RequestParam String chatName) {
        List<UserChatDTO> userChatDTOS = chatService.getUsersForChat(chatName);
        if (userChatDTOS.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userChatDTOS);
        }
        return ResponseEntity.ok(userChatDTOS);
    }

    @PostMapping("/api/chatAdd")
    public ResponseEntity<?> addChatMessage(@Valid @RequestBody MessageChatDTO messageChatDTO) {
        Optional<MessageChatDTO> savedMessage = chatService.addChatMessage(messageChatDTO);
        if (savedMessage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found");
        }
        return ResponseEntity.ok(savedMessage.get());
    }

    @PostMapping("/api/chatCreate")
    public ResponseEntity<?> createChat(@RequestParam String name) {
        Optional<Chat> chat = chatService.createOrCheckChat(name);
        if (chat.isPresent()) {
            return ResponseEntity.ok(chat.get());
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found");
    }

    @GetMapping("/api/oldMessages")
    public ResponseEntity<?> loadOldMessages(@RequestParam String chatName, @RequestHeader("Authorization") String token) {
        List<MessageChatDTO> messageChatDTOS = chatService.getOldChatMessages(chatName, token);
        if (!messageChatDTOS.isEmpty()) {
            return ResponseEntity.ok(messageChatDTOS);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found");
    }

    @GetMapping("/api/newMessages")
    public ResponseEntity<?> loadNewMessages(@RequestParam String chatName, @RequestHeader("Authorization") String token) {
        List<MessageResponseDTO> messageResponseDTOs = chatService.getNewChatMessages(chatName, token);
        if (!messageResponseDTOs.isEmpty()) {
            return ResponseEntity.ok(messageResponseDTOs);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found");
    }

    @GetMapping("/api/checkMessages")
    public  ResponseEntity<?> checkMessages(@RequestParam String chatName, @RequestHeader("Authorization") String token) {
        Boolean messages = chatService.checkNewMessages(chatName, token);
        if (messages != null) {
            return ResponseEntity.ok(messages);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Chat not found");
    }



}
