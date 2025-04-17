package com.example.webchat.controller;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.model.Profile;
import com.example.webchat.model.User;
import com.example.webchat.service.ImageService;
import com.example.webchat.service.UserService;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final ImageService imageService;
    private final UserService userService;

    @PostMapping("/profile")
    public ResponseEntity<HashMap<String, String>> profile(@Valid @RequestBody ProfileDTO profileDTO) {
        // Perform login logic here
        // For example, you can save the username in the session or perform authentication
        log.info("Profile " + profileDTO.toString());
        Optional<Profile> profile = profileService.createProfile(profileDTO);
        HashMap<String, String> response = new HashMap<>();

        if (profile.isEmpty()) {
            response.put("message", "Profile creation failed");
            response.put("success", "false");
            return ResponseEntity.badRequest().body(response);
        }
        response.put("message", "Profile created successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            User user = userService.getAuthenticatedUser();
            log.info("User upload a photo " + user.getUsername());
            Long imageId = imageService.saveImage(file, user.getUserID());
            return ResponseEntity.ok("Image uploaded successfully with ID: " + imageId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }

    @GetMapping("/getImage")
    public ResponseEntity<byte[]> getImage() {
        try {
            User user = userService.getAuthenticatedUser();
            byte[] imageData = imageService.getImageByUserId(user.getUserID()).getData();
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg") // Adjust based on your image type
                    .body(imageData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}
