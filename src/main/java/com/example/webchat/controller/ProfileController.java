package com.example.webchat.controller;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.HashMap;

@Slf4j
@AllArgsConstructor
@Controller
public class ProfileController {

    private final ProfileService profileService;

    @PostMapping("/profile")
    public ResponseEntity<HashMap<String, String>> profile(@Valid @RequestBody ProfileDTO profileDTO) {
        // Perform login logic here
        // For example, you can save the username in the session or perform authentication

        //profileService.
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "User logged in successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }
}
