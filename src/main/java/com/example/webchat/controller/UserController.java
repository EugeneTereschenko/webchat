package com.example.webchat.controller;

import com.example.webchat.dto.UserDTO;
import com.example.webchat.model.User;
import com.example.webchat.security.JwtUtil;
import com.example.webchat.service.EmailNotificationService;
import com.example.webchat.service.QRCodeGenerator;
import com.example.webchat.service.TwoFactorAuthService;
import com.example.webchat.service.UserService;
import com.example.webchat.service.impl.ActivityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    private final TwoFactorAuthService twoFactorAuthService;
    private final EmailNotificationService emailService;
    private final QRCodeGenerator qrCodeGenerator;
    private final JwtUtil jwtUtil;



    @PostMapping("api/singup")
    public ResponseEntity<HashMap<String, String>> register(@Valid @RequestBody UserDTO userDTO) {

        User existingUser = userService.getUserByEmail(userDTO.getEmail());
        if (existingUser != null) {
            HashMap<String, String> response = new HashMap<>();
            response.put("message", "User with this email already exists");
            return ResponseEntity.badRequest().body(response);
        }

        User user = userService.registerUser(userDTO);
        log.info("Registering user: " + user.toString());
        HashMap<String, String> response = new HashMap<>();
        response.put("message", "User " + user.getUsername() + " registered successfully");
        activityService.addActivity("User registered", user.getUserID(), new Date());
        return ResponseEntity.ok(response);
    }


    @PostMapping("api/login")
    public ResponseEntity<HashMap<String, String>> login(@Valid @RequestBody UserDTO userDTO) {
        HashMap<String, String> response = new HashMap<>();
        User user = userService.getUserByEmail(userDTO.getEmail());

        if (user == null) {
            response.put("message", "Access denied");
            response.put("success", "false");
            return ResponseEntity.badRequest().body(response);
        }

        response = createResponse(userDTO, user);
        return ResponseEntity.ok(response);
    }


    @PostMapping("api/verify")
    public ResponseEntity<HashMap<String, String>> verifyOtp(@Valid @RequestBody UserDTO userDTO) {
        HashMap<String, String> response = new HashMap<>();
        User user = userService.getUserByEmail(userDTO.getEmail());
        log.info("verify otp login");
        if (user.isTwoFactorEnabled()) {
            if (!isTwoFactorCodeValid(user, userDTO.getUserCode())) {
                log.info("Invalid otp login");
                response.put("message", "Invalid two-factor authentication code");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        }
        log.info("Create response");
        activityService.addActivity("User login", user.getUserID(), new Date());

        String token = jwtUtil.generateToken(user.getUsername());
        log.info("token: " + token);
        log.info("Login attempt for user: " + userDTO.toString());
        response.put("token", token);
        response.put("userID", String.valueOf(user.getUserID()));
        response.put("message", "User logged in successfully");
        response.put("success", "true");

        return ResponseEntity.ok(response);
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
            String userCode = twoFactorAuthService.generateCode(user.getSalt());
            String qrCodeBase64 = null;
            String body = null;
            try {
                qrCodeBase64 = QRCodeGenerator.generateQRCode("otpauth://totp/user code " + userCode);
                body = "Your code is: " + userCode + "\n\nPlease find your QR code attached.";
                emailService.sendEmailWithAttachment("test@example.com", "Your QR Code", body, qrCodeBase64);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
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
    public ResponseEntity<HashMap<String, String>> changePassword(@RequestBody UserDTO userDTO) {

        HashMap<String, String> response = new HashMap<>();
        log.info("Change password request: " + userDTO.toString());
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty() || userDTO.getNewPassword() == null || userDTO.getNewPassword().isEmpty()) {
            response.put("message", "Current password or new password is missing");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            userService.changePassword(userDTO.getPassword(), userDTO.getNewPassword());
            response.put("message", "Password changed successfully");
            User user = userService.getAuthenticatedUser();
            activityService.addActivity("Change password", user.getUserID(), new Date());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to change password: " + e.getMessage());
            return ResponseEntity.status(401).body(response);
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
    public ResponseEntity<HashMap<String, String>> twoFactorAuthentication(@RequestParam String twoFactors) {
        HashMap<String, String> response = new HashMap<>();
        try {
            userService.twoFactors(twoFactors);
            response.put("message", "Two-factor update successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("message", "Failed to update two-factor" + e.getMessage());
            return ResponseEntity.status(401).body(response);
        }
    }

    private HashMap<String, String> createResponse(UserDTO userDTO, User user) {
        HashMap<String, String> response = new HashMap<>();
        activityService.addActivity("User login", user.getUserID(), new Date());
        log.info("request for User controller. login: " + userDTO.getPassword() + " " + userDTO.getUsername());
        String token = userService.authenticateUser(Optional.ofNullable(user.getUsername()).orElse(userDTO.getUsername()), userDTO.getPassword());
        log.info("token: " + token);
        log.info("Login attempt for user: " + userDTO.toString());
        response.put("token", token);
        response.put("userID", String.valueOf(user.getUserID()));
        response.put("message", "User logged in successfully");
        response.put("success", "true");
        return response;
    }

    private boolean isTwoFactorCodeValid(User user, String userCode) {
        try {
            return twoFactorAuthService.verifyCode(user.getSecretKey(), Integer.parseInt(userCode));
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
