package com.example.webchat.controller;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.dto.ProfileResponseDTO;
import com.example.webchat.model.Profile;
import com.example.webchat.model.User;
import com.example.webchat.service.ImageService;
import com.example.webchat.service.UserService;
import com.example.webchat.service.impl.ActivityService;
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
    private final ActivityService activityService;

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
        } else {
            log.debug("Profile not found");
            return ResponseEntity.status(404).body("Profile not found");
        }
    }

    @GetMapping("api/allProfiles")
    public ResponseEntity<?> getAllProfiles() {
        List<ProfileDTO> profileDTO = profileService.getAllProfiles();
        if (!profileDTO.isEmpty()) {
            log.debug("Get all profiles " + profileDTO.toString());
            return ResponseEntity.ok(profileDTO);
        } else {
            log.debug("No profiles found");
            return ResponseEntity.status(404).body("No profiles found");
        }
    }


    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            User user = userService.getAuthenticatedUser();
            log.info("User upload a photo " + user.getUsername());
            Long imageId = imageService.saveImage(file, user.getUserID());
            activityService.addActivity("Upload image", user.getUserID(), new Date());
            return ResponseEntity.ok("Image uploaded successfully with ID: " + imageId);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error uploading image: " + e.getMessage());
        }
    }

    @GetMapping("/getImage")
    public ResponseEntity<byte[]> getImage() {
        try {
            User user = userService.getAuthenticatedUser();
            byte[] imageData = imageService.getImageByUserId(user.getUserID()).get().getData();
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg") // Adjust based on your image type
                    .body(imageData);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
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
        log.info("Get user activity " + numOfLogs);
        User user = userService.getAuthenticatedUser();
        HashMap<String, String> response = activityService.getActivitiesByUserId(user.getUserID(), Integer.valueOf(numOfLogs));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/updateNotification")
    public ResponseEntity<HashMap<String, String>> updateNotification(@RequestParam String notification) {
        log.info("Update user notification " + notification);
        User user = userService.getAuthenticatedUser();
        HashMap<String, String> response = new HashMap<>();
        if (notification.equals("true")) {
            Boolean result = profileService.updateNotification(user.getUsername(), true);
            activityService.addActivity("Update add Email notifications", user.getUserID(), new Date());
            response.put("message", "Notification updated successfully");
            response.put("success", String.valueOf(result));
        } else {
            Boolean result = profileService.updateNotification(user.getUsername(), false);
            response.put("message", "Notification updated successfully");
            response.put("success", String.valueOf(result));
        }
        return ResponseEntity.ok(response);
    }

}
