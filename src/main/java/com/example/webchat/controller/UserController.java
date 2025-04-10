package com.example.webchat.controller;

import com.example.webchat.dto.UserDTO;
import com.example.webchat.model.User;
import com.example.webchat.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public ModelAndView login() {
        // Perform login logic here
        // For example, you can save the username in the session or perform authentication

        log.info("Login attempt for user: ");
        return new ModelAndView("login");
    }

    @GetMapping("/singup")
    public ModelAndView singup() {
        // Perform login logic here
        // For example, you can save the username in the session or perform authentication

        log.info("Login attempt for user: ");
        return new ModelAndView("singup");
    }


    @PostMapping("api/singup")
    public ResponseEntity<HashMap<String, String>> register(@Valid @RequestBody UserDTO userDTO) {

        User user = userService.registerUser(userDTO);
        log.info("Registering user: " + user.toString());
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "User " + user.getUsername() + " registered successfully");
        return ResponseEntity.ok(response);
    }

    @PostMapping("api/login")
    public ResponseEntity<HashMap<String, String>> login(@Valid @RequestBody UserDTO userDTO) {
        // Perform login logic here
        // For example, check the username and password against the database
        User user = userService.getUserByEmail(userDTO.getEmail());
        log.info("request for User controller. login: " + userDTO.getPassword() + " " + userDTO.getUsername());
        String token = userService.authenticateUser(Optional.ofNullable(user.getUsername()).orElse(userDTO.getUsername()), userDTO.getPassword());
        log.info("token: " + token);
        log.info("Login attempt for user: " + userDTO.toString());
        HashMap<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("userID", String.valueOf(user.getUserID()));
        response.put("message", "User logged in successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

}
