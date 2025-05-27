package com.example.webchat.controller;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.dto.ProfileResponseDTO;
import com.example.webchat.service.ImageService;
import com.example.webchat.service.UserService;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.*;

@Slf4j
@AllArgsConstructor
@Controller
public class ProfileController {

    private final ProfileService profileService;
    private final ImageService imageService;
    private final UserService userService;

    @PostMapping("/profile")
    public ResponseEntity<?> profile(@Valid @RequestBody ProfileDTO profileDTO) {
        log.debug("ProfileDTO " + profileDTO.toString());
        Optional<ProfileResponseDTO> profileResponseDTO = profileService.createProfile(profileDTO);
        if (profileResponseDTO.isEmpty()) {
            return ResponseEntity.badRequest().body("Profile creation failed");
        }
        return ResponseEntity.ok(profileResponseDTO);
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        log.debug("Update profileDTO " + profileDTO.toString());
        Optional<ProfileResponseDTO> profileResponseDTO = profileService.updateProfile(profileDTO);
        if (profileResponseDTO.isEmpty()) {
            return ResponseEntity.badRequest().body("Profile update failed");
        }
        return ResponseEntity.ok().body(profileResponseDTO);
    }

    @GetMapping("api/profile")
    public ResponseEntity<?> getProfile() {
        log.debug("Get profile");
        Optional<ProfileDTO> profileDTO = profileService.getProfile();
        if (profileDTO.isPresent()) {
            log.debug("Get profile " + profileDTO.get().toString());
            return ResponseEntity.ok(profileDTO.get());
        }
        log.debug("Profile not found");
        return ResponseEntity.status(404).body("Profile not found");
    }

    @GetMapping("api/allProfiles")
    public ResponseEntity<?> getAllProfiles() {
        List<ProfileDTO> profileDTO = profileService.getAllProfiles();
        if (!profileDTO.isEmpty()) {
            log.debug("Get all profiles " + profileDTO.toString());
            return ResponseEntity.ok(profileDTO);
        }
        log.debug("No profiles found");
        return ResponseEntity.status(404).body("No profiles found");
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String result = imageService.uploadImageToDatabase(file);
        if (!result.startsWith("Error")) {
            log.debug("Image upload successful: " + result);
            return ResponseEntity.ok(result);
        }
        log.error("Image upload failed: " + result);
        return ResponseEntity.status(500).body(result);
    }

    @GetMapping("/getImage")
    public ResponseEntity<byte[]> getImage() {
        byte[] imageDataResult = imageService.getImageForUser();
        if (imageDataResult != null) {
            log.debug("Image retrieval successful");
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg") // Adjust based on your image type
                    .body(imageDataResult);
        }
        log.error("Image retrieval failed");
        return ResponseEntity.status(500).body(null);
    }

    @GetMapping("/locked")
    public ResponseEntity<?> locked() {
        Optional<ProfileResponseDTO> profileResponseDTO = userService.deactivateUser();
        if (!profileResponseDTO.isEmpty()) {
            return ResponseEntity.ok(profileResponseDTO);
        }
        return ResponseEntity.badRequest().body("User is already locked or does not exist");
    }

    @GetMapping("/unlocked")
    public ResponseEntity<?> unlocked() {
        Optional<ProfileResponseDTO> profileResponseDTO = userService.activateUser();
        if (!profileResponseDTO.isEmpty()) {
            return ResponseEntity.ok(profileResponseDTO);
        }
        return ResponseEntity.badRequest().body("User is already unlocked or does not exist");
    }

    @GetMapping("/activity")
    public ResponseEntity<HashMap<String, String>> getActivity(@RequestParam String numOfLogs) {
        HashMap<String, String> response = profileService.getActivityByUser(numOfLogs);
        if (!response.isEmpty()) {
            log.debug("Get user activity " + numOfLogs);
            return ResponseEntity.ok(response);
        }
        log.warn("No activity found for user with numOfLogs: " + numOfLogs);
        return ResponseEntity.status(404).body(new HashMap<String, String>() {{
            put("message", "No activity found for user");
        }});
    }

    @GetMapping("/updateNotification")
    public ResponseEntity<ProfileResponseDTO> updateNotification(@RequestParam String notification) {
        log.debug("Update user notification " + notification);
        Optional<ProfileResponseDTO> profileResponseDTO = profileService.getUpdateNotification(notification);
        if (profileResponseDTO.isPresent()) {
            log.debug("Notification updated successfully");
            return ResponseEntity.ok(profileResponseDTO.get());
        }
        log.error("Failed to update notification");
        return ResponseEntity.status(500).body(new ProfileResponseDTO("Failed to update notification", "false"));
    }

    @GetMapping("/updateMessage")
    public ResponseEntity<ProfileResponseDTO> updateMessage(@RequestParam String message) {
        log.debug("Update user message " + message);
        Optional<ProfileResponseDTO> profileResponseDTO = profileService.getUpdateMessage(message);
        if (profileResponseDTO.isPresent()) {
            log.debug("Message updated successfully");
            return ResponseEntity.ok(profileResponseDTO.get());
        }
        log.error("Failed to update message");
        return ResponseEntity.status(500).body(new ProfileResponseDTO("Failed to update message", "false"));
    }


}
