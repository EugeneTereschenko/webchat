package com.example.webchat.controller;

import com.example.webchat.dto.MessageDTO;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

@AllArgsConstructor
@Controller
public class ChatController {

    private final List<String> Users;
    private final List<MessageDTO> messagesDTO;
    private final UserService userService;


    //@PreAuthorize("userService.isAuthenticated()")
    @GetMapping("/")
    public String home(Model model) {

        User user = userService.getAuthenticatedUser();
        if (user == null) {
            System.out.println("User is not authenticated");
            return "redirect:/api/login"; // Redirect to login page if user is not authenticated
        }
        System.out.println("Authenticated user: " + user.toString());
        if (!Users.contains(user.getUsername())) {
            Users.add(user.getUsername());
        }
        model.addAttribute("usermessage", user.getUsername());
        //model.addAttribute("users", Users);
        //model.addAttribute("messages", messages);
        model.addAttribute("message", new Message());
        return "index"; // Refers to "index.html" in src/main/resources/templates/
    }

    //@PreAuthorize("userService.isAuthenticated()")
    @PostMapping(value = "/saveMessage")
    public ResponseEntity<HashMap<String, String>> saveMessage(@Valid @RequestBody com.example.webchat.dto.MessageDTO messageDTO) {

        String username = messageDTO.getUser();
        String messageContent = messageDTO.getMessage();
        System.out.println("Received message: " + username + " - " + messageContent);
        messagesDTO.add(new MessageDTO(messageDTO.getUser(), messageDTO.getMessage()));


        HashMap<String, String> response = new HashMap<>();
        response.put("message", "User logged in successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    //@PreAuthorize("userService.isAuthenticated()")
    @GetMapping("/messages")
    @ResponseBody
    public List<MessageDTO> getMessagesDTO() {
        return messagesDTO;
    }

    @GetMapping("/users")
    @ResponseBody
    public List<String> getUsers() {
        User user = userService.getAuthenticatedUser();
        if (user == null) {
            System.out.println("User is not authenticated");
        }
        System.out.println("Authenticated user: " + user.toString());
        if (!Users.contains(user.getUsername())) {
            Users.add(user.getUsername());
        }
        return Users;
    }




}
