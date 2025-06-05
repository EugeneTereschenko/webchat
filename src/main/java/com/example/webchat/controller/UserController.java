package com.example.webchat.controller;

import com.example.webchat.dto.UserDTO;
import com.example.webchat.dto.UserResponseDTO;
import com.example.webchat.model.User;
import com.example.webchat.service.UserService;
import com.example.webchat.service.impl.ActivityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;

@Slf4j
@RestController
@AllArgsConstructor
public class UserController {


    private final UserService userService;
    private final ActivityService activityService;



    @PostMapping("api/singup")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        User existingUser = userService.getUserByEmail(userDTO.getEmail());
        if (existingUser != null) {
            userResponseDTO.setMessage("User with this email already exists");
            return ResponseEntity.badRequest().body(userResponseDTO);
        }
        userResponseDTO = userService.registerAndAddRole(userDTO);
        if(userResponseDTO.getSuccess() == null || userResponseDTO.getSuccess().equals("false")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponseDTO);
        }
        return ResponseEntity.ok(userResponseDTO);
    }


    @PostMapping("api/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        User user = userService.getUserByEmail(userDTO.getEmail());
        if (user == null) {
            userResponseDTO.setMessage("Access denied");
            userResponseDTO.setSuccess("false");
            return ResponseEntity.badRequest().body(userResponseDTO);
        }
        userResponseDTO = userService.loginUser(userDTO, user);
        if(userResponseDTO.getSuccess() == null || userResponseDTO.getSuccess().equals("false")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponseDTO);
        }
        return ResponseEntity.ok(userResponseDTO);
    }


    @PostMapping("api/verify")
    public ResponseEntity<UserResponseDTO> verifyOtp(@Valid @RequestBody UserDTO userDTO) {
        User user = userService.getUserByEmail(userDTO.getEmail());
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new UserResponseDTO("User not found", "false"));
        }
        UserResponseDTO userResponseDTO = userService.verifyOtpAndLogin(user, userDTO);
        log.info("Verify OTP request userResponseDTO: " + userResponseDTO.toString());
        if (userResponseDTO.getSuccess() == null || userResponseDTO.getSuccess().equals("false")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponseDTO);
        }
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping("api/check-auth")
    public ResponseEntity<UserResponseDTO> checkAuth(@Valid @RequestBody UserDTO userDTO) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        User user = userService.getUserByEmail(userDTO.getEmail());
        log.info("Check auth request: " + userDTO.toString());
        if (user == null) {
            userResponseDTO.setMessage("User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(userResponseDTO);
        }
        userResponseDTO = userService.checkAuth(user);
        if (userResponseDTO.getSuccess() == null || userResponseDTO.getSuccess().equals("false")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(userResponseDTO);
        }
        return ResponseEntity.ok(userResponseDTO);
    }

    @PutMapping("api/change-password")
    public ResponseEntity<UserResponseDTO> changePassword(@RequestBody UserDTO userDTO) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        log.info("Change password request: " + userDTO.toString());
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty() || userDTO.getNewPassword() == null || userDTO.getNewPassword().isEmpty()) {
            userResponseDTO.setMessage("Current password or new password is missing");
            return ResponseEntity.badRequest().body(userResponseDTO);
        }

        try {
            userService.changePassword(userDTO.getPassword(), userDTO.getNewPassword());
            userResponseDTO.setMessage("Password changed successfully");
            User user = userService.getAuthenticatedUser();
            activityService.addActivity("Change password", user.getUserID(), new Date());
            return ResponseEntity.ok(userResponseDTO);
        } catch (Exception e) {
            userResponseDTO.setMessage("Failed to change password: " + e.getMessage());
            return ResponseEntity.status(401).body(userResponseDTO);
        }
    }

    @PostMapping("api/refresh-token")
    public ResponseEntity<UserResponseDTO> refreshToken(@RequestBody HashMap<String, String> request) {
        String currentToken = request.get("token");
        UserResponseDTO userResponseDTO = new UserResponseDTO();

        if (currentToken == null || currentToken.isEmpty()) {
            userResponseDTO.setMessage("Token is missing");
            return ResponseEntity.badRequest().body(userResponseDTO);
        }

        try {
            String newToken = userService.refreshToken(currentToken);
            userResponseDTO.setToken(newToken);
            userResponseDTO.setMessage("Token refreshed successfully");
            return ResponseEntity.ok(userResponseDTO);
        } catch (Exception e) {
            userResponseDTO.setMessage("Failed to refresh token: " + e.getMessage());
            return ResponseEntity.status(401).body(userResponseDTO);
        }
    }

    @GetMapping("api/twoFactors")
    public ResponseEntity<UserResponseDTO> twoFactorAuthentication(@RequestParam String twoFactors) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        try {
            userService.twoFactors(twoFactors);
            userResponseDTO.setMessage("Two-factor update successfully");
            return ResponseEntity.ok(userResponseDTO);
        } catch (Exception e) {
            userResponseDTO.setMessage("Failed to update two-factor" + e.getMessage());
            return ResponseEntity.status(401).body(userResponseDTO);
        }
    }


    @GetMapping("api/allUsers")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsersDTO());
        } catch (Exception e) {
            log.error("Error fetching all users: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching users");
        }
    }
}
