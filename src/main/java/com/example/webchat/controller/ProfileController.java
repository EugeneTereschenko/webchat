package com.example.webchat.controller;

import com.example.webchat.dto.ProfileDTO;
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
    public ResponseEntity<HashMap<String, String>> profile(@Valid @RequestBody ProfileDTO profileDTO) {
        log.info("Profile " + profileDTO.toString());
        User user = userService.getAuthenticatedUser();
        Optional<Profile> profile = profileService.createProfile(profileDTO);
        HashMap<String, String> response = new HashMap<>();

        if (profile.isEmpty()) {
            response.put("message", "Profile creation failed");
            response.put("success", "false");
            return ResponseEntity.badRequest().body(response);
        }
        activityService.addActivity("Profile create", user.getUserID(), new Date());
        response.put("message", "Profile created successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    @PutMapping("/profile")
    public ResponseEntity<HashMap<String, String>> updateProfile(@Valid @RequestBody ProfileDTO profileDTO) {
        log.info("Update profile " + profileDTO.toString());
        HashMap<String, String> response = new HashMap<>();
        try {
            User user = userService.getAuthenticatedUser();
            profileService.updateProfile(profileDTO);
            response.put("message", "Profile updated successfully");
            response.put("success", "true");
            String token = userService.changeUsername(user.getUsername(), profileDTO.getUsername());
            response.put("token", token);
            response.put("userID", String.valueOf(user.getUserID()));
            activityService.addActivity("Profile updated", user.getUserID(), new Date());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Profile update failed");
            response.put("success", "false");
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("api/profile")
    public ResponseEntity<ProfileDTO> getProfile() {
        try {
            User user = userService.getAuthenticatedUser();
            Optional<ProfileDTO> profileDTO = profileService.getProfileByUserId(user.getUserID());
            profileDTO.get().setUsername(user.getUsername());
            profileDTO.get().setEmail(user.getEmail());
            profileDTO.get().setIsActive(String.valueOf(user.isActive()));
            profileDTO.get().setNotification(String.valueOf(false));
            profileDTO.get().setTwoFactors(String.valueOf(user.isTwoFactorEnabled()));
            log.info("Get profile " + profileDTO.get().toString());
            if (profileDTO.isPresent()) {
                return ResponseEntity.ok(profileDTO.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            log.info(e + " Exception get profile");
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("api/allProfiles")
    public ResponseEntity<List<ProfileDTO>> getAllProfiles() {
        try {
            User user = userService.getAuthenticatedUser();
            List<ProfileDTO> profileDTO = profileService.getAllProfiles(user.getUserID());
            log.info("Get all profiles " + profileDTO.toString());
            if (!profileDTO.isEmpty()) {
                return ResponseEntity.ok(profileDTO);
            } else {
                return ResponseEntity.ok(new ArrayList<>());
            }
        } catch (Exception e) {
            log.info(e + " Exception get all profiles");
            return ResponseEntity.status(500).body(null);
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
    public ResponseEntity<HashMap<String, String>> locked() {
        User user = userService.getAuthenticatedUser();
        userService.deactivateUser(user.getUsername());

        HashMap<String, String> response = new HashMap<>();
        response.put("message", "User is locked");
        response.put("success", "false");
        activityService.addActivity("User is locked", user.getUserID(), new Date());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unlocked")
    public ResponseEntity<HashMap<String, String>> unlocked() {
        User user = userService.getAuthenticatedUser();
        userService.activateUser(user.getUsername());

        HashMap<String, String> response = new HashMap<>();
        response.put("message", "User is unlocked");
        response.put("success", "true");
        activityService.addActivity("User is unlocked", user.getUserID(), new Date());
        return ResponseEntity.ok(response);
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
