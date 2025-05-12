package com.example.webchat.service;

import com.example.webchat.dto.UserDTO;
import com.example.webchat.dto.UserResponseDTO;
import com.example.webchat.exception.UserBlockedException;
import com.example.webchat.model.Role;
import com.example.webchat.model.User;
import com.example.webchat.repository.RoleRepository;
import com.example.webchat.repository.UserRepository;
import com.example.webchat.security.JwtUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final RoleRepository roleRepo;
    private final TwoFactorAuthService twoFactorAuthService;
    private final EmailNotificationService emailService;
    private final ActivityServiceImpl activityService;


    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean isAuthenticated() {
        log.debug("Checking if user is authenticated");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName() + " " + authentication.isAuthenticated());
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            log.debug("User is not authenticated");
            return false;
        }
        return authentication.isAuthenticated();
    }

    public User registerUser(UserDTO registrationDTO) {
        User userAdd = userRepository.findByUsername(Optional.of(registrationDTO.getUsername()).orElse("_"));
        if (userAdd != null) {
            throw new UserBlockedException("User already exists");
        }
        User userNew = new User();
        String passwordSalt = null;


        userNew.setUsername(Optional.ofNullable(registrationDTO.getUsername()).orElse(registrationDTO.getUsername()));
        userNew.setActive(Optional.ofNullable(Boolean.parseBoolean(registrationDTO.getIsActive())).orElse(false));
        userNew.setEmail(Optional.ofNullable(registrationDTO.getEmail()).orElse(""));
        passwordSalt = Optional.ofNullable(registrationDTO.getPassword()).orElse(generatePassayPassword(8));
        userNew.setPassword(passwordEncoder.encode(passwordSalt));
        userNew.setTwoFactorEnabled(false);
        userNew.setSalt(passwordSalt);

        log.debug("create user" + userNew.getUsername());
        return userRepository.save(userNew);
    }

    public Role saveRole(Role role) {
        log.debug("Saving new user to the database", role.getName());
        return roleRepo.save(role);
    }

    public Role createRoleIfExists(String roleName) {
        log.debug("Checking if role exists: " + roleName);
        Role existingRole = roleRepo.findByName(roleName);
        if (existingRole != null) {
            log.debug("Role already exists: " + roleName);
            return existingRole;
        }
        Role role = new Role();
        role.setName(roleName);
        return saveRole(role);
    };

    public void addRoleToUser(String username, String roleName) {
        log.debug("Adding role {} to user {}", roleName, username);
        User user = userRepository.findByUsername(username);
        Role role = roleRepo.findByName(roleName);
        user.getRoles().add(role);
    }

    public String generatePassayPassword(int length) {
        PasswordGenerator gen = new PasswordGenerator();
        EnglishCharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        EnglishCharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        EnglishCharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);


        return gen.generatePassword(length, lowerCaseRule,
                upperCaseRule, digitRule);
    }

    public boolean isPasswordValid(User user, String oldPassword) {
        User userSearch = userRepository.findByUsername(user.getUsername());
        if (userSearch == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    public void changePassword(String password, String newPassword) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        if (!isPasswordValid(user, password)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setSalt(newPassword);
        userRepository.save(user);
    }

    public boolean deactivateUser(String username){
        User userSearch = userRepository.findByUsername(username);
        if (userSearch == null) {
            throw new UsernameNotFoundException("User not found");
        }
        userSearch.setActive(false);
        User userSave = userRepository.save(userSearch);
        if (userSave != null){
            return true;
        }
        return false;
    }

    public boolean activateUser(String username){
        User userSearch = userRepository.findByUsername(username);
        if (userSearch == null) {
            throw new UsernameNotFoundException("User not found");
        }
        userSearch.setActive(true);
        User userSave = userRepository.save(userSearch);
        if (userSave != null){
            return true;
        }
        return false;
    }


    public String authenticateUser(String username, String password) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtUtil.generateToken(userDetails.getUsername());
    }

    public User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication instanceof AnonymousAuthenticationToken) {
            throw new UsernameNotFoundException("User not authenticated");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username);
    }

    public String refreshToken(String currentToken) {
        if (currentToken == null || currentToken.isEmpty()) {
            throw new IllegalArgumentException("Current token is missing or invalid");
        }
        String username = jwtUtil.extractUsername(currentToken);

        if (!jwtUtil.validateToken(currentToken, username)) {
            throw new SecurityException("Invalid or expired token");
        }

        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return jwtUtil.generateToken(username);
    }

    public String changeUsername(String oldUsername, String newUsername) {
        User user = userRepository.findByUsername(oldUsername);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (userRepository.findByUsername(newUsername) != null) {
            throw new IllegalArgumentException("Username already taken");
        }

        user.setUsername(newUsername);
        userRepository.save(user);

        // Generate a new token for the updated username
        String newToken = jwtUtil.generateToken(newUsername);
        log.debug("New token generated for updated username: " + newToken);


        return newToken;
    }

    public void twoFactors(String twoFactors) {
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new UsernameNotFoundException("Authenticated user not found");
        }

        boolean isTwoFactorEnabled = Optional.ofNullable(twoFactors)
                .map(Boolean::parseBoolean)
                .orElse(false);

        if (isTwoFactorEnabled && user.getSecretKey() == null) {
            String secretKey = twoFactorAuthService.generateSecretKey();
            user.setSecretKey(secretKey);
        }

        user.setTwoFactorEnabled(isTwoFactorEnabled);
        userRepository.save(user);
    }


    public void prepareAndSendTwoFactorEmailMessage(User user){
        String userCode = twoFactorAuthService.generateCode(user.getSecretKey());
        String qrCodeBase64 = null;
        String body = null;
        try {
            qrCodeBase64 = QRCodeGenerator.generateQRCode("otpauth://totp/user code " + userCode);
            body = "Your code is: " + userCode + "\n\nPlease find your QR code attached.";
            emailService.sendEmailWithAttachment("test@example.com", "Your QR Code", body, qrCodeBase64);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public UserResponseDTO verifyOtpAndLogin(User user, UserDTO userDTO) {
        UserResponseDTO responseDTO = new UserResponseDTO();
        log.info("Create response");
        try {
            boolean isOtpValid = twoFactorAuthService.verifyCode(user.getSecretKey(), Integer.parseInt(userDTO.getUserCode()));
            if (isOtpValid) {
                String token = jwtUtil.generateToken(user.getUsername());
                responseDTO.setSuccess("true");
                responseDTO.setMessage("OTP verified successfully");
                responseDTO.setToken(token);
                responseDTO.setUserID(String.valueOf(user.getUserID()));
                activityService.addActivity("User login", user.getUserID(), new Date());
            } else {
                responseDTO.setSuccess("false");
                responseDTO.setMessage("Invalid OTP");
            }
        } catch (Exception e) {
            responseDTO.setSuccess("false");
            responseDTO.setMessage("Error verifying OTP: " + e.getMessage());
        }
        return responseDTO;
    }
}
