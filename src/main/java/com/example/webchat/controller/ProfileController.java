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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        return ResponseEntity.ok(response);
    }

    @GetMapping("/unlocked")
    public ResponseEntity<HashMap<String, String>> unlocked() {
        User user = userService.getAuthenticatedUser();
        userService.activateUser(user.getUsername());

        HashMap<String, String> response = new HashMap<>();
        response.put("message", "User is unlocked");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/activity")
    public ResponseEntity<HashMap<String, String>> getActivity(@RequestParam String numOfLogs) {
        //User user = userService.getAuthenticatedUser();
        log.info("Get user activity " + numOfLogs);
        HashMap<String, String> response = new HashMap<>();
        response.put("Updated profile picture", "2 hours ago");
        response.put("Updated profile", "1 hour ago");
        response.put("Last login", "3 days ago");
        response.put("message", "User activity retrieved successfully");
        response.put("success", "true");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ModelAndView profile() {
        // Perform login logic here
        // For example, you can save the username in the session or perform authentication

        log.info("Login attempt for user: ");
        return new ModelAndView("profile");
    }

    @GetMapping("/bio")
    public ModelAndView bio() {
        // Perform login logic here
        // For example, you can save the username in the session or perform authentication

        log.info("Login attempt for user: ");
        return new ModelAndView("bio");
    }
}
