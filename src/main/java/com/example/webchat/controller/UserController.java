package com.example.webchat.controller;

import com.example.webchat.dto.UserDTO;
import com.example.webchat.dto.UserResponseDTO;
import com.example.webchat.model.User;
import com.example.webchat.security.JwtUtil;
import com.example.webchat.service.EmailNotificationService;
import com.example.webchat.service.QRCodeGenerator;
import com.example.webchat.service.TwoFactorAuthService;
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
import java.util.Optional;

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

        User user = userService.registerUser(userDTO);
        userService.createRoleIfExists("ROLE_USER");
        userService.addRoleToUser(user.getUsername(), "ROLE_USER");
        log.info("Registering user: " + user.toString());
        userResponseDTO.setMessage("User " + user.getUsername() + " registered successfully");
        activityService.addActivity("User registered", user.getUserID(), new Date());
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

        userResponseDTO = createResponse(userDTO, user);
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
    public ResponseEntity<HashMap<String, String>> checkAuth(@Valid @RequestBody UserDTO userDTO) {
        HashMap<String, String> response = new HashMap<>();
        User user = userService.getUserByEmail(userDTO.getEmail());
        log.info("Check auth request: " + userDTO.toString());
        if (user == null) {
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if (user.isTwoFactorEnabled()) {
            log.info("User has two-factor authentication enabled");
            response.put("twofactor", "true");
            userService.prepareAndSendTwoFactorEmailMessage(user);
            response.put("success", "true");
            response.put("message", "Two-factor code sent to your email");
            return ResponseEntity.ok(response);
        }
        response.put("success", "false");
        response.put("twofactor", "false");
        response.put("message", "User is authenticated by default");
        return ResponseEntity.ok(response);
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
    public ResponseEntity<HashMap<String, String>> refreshToken(@RequestBody HashMap<String, String> request) {
        String currentToken = request.get("token");
        HashMap<String, String> response = new HashMap<>();

        if (currentToken == null || currentToken.isEmpty()) {
            response.put("message", "Token is missing");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            String newToken = userService.refreshToken(currentToken);
            response.put("token", newToken);
            response.put("message", "Token refreshed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to refresh token: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
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

    private UserResponseDTO createResponse(UserDTO userDTO, User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        activityService.addActivity("User login", user.getUserID(), new Date());
        log.debug("request for User controller. login: " + userDTO.getUsername());
        String token = userService.authenticateUser(Optional.ofNullable(user.getUsername()).orElse(userDTO.getUsername()), userDTO.getPassword());
        log.info("token: " + token);
        log.debug("Login attempt for user: " + userDTO.toString());
        userResponseDTO.setToken(token);
        userResponseDTO.setUserID(String.valueOf(user.getUserID()));
        userResponseDTO.setMessage("User logged in successfully");
        userResponseDTO.setSuccess("true");

        return userResponseDTO;
    }

}
