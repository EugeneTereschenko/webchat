package com.example.webchat.service;

import com.example.webchat.dto.UserDTO;
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
        log.info("Checking if user is authenticated");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info(authentication.getName() + " " + authentication.isAuthenticated());
        if (authentication == null || AnonymousAuthenticationToken.class.
                isAssignableFrom(authentication.getClass())) {
            log.info("User is not authenticated");
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

        log.info(userNew.toString() + " " + userNew.getUsername() + " " + userNew.getPassword());
        return userRepository.save(userNew);
    }

    public Role saveRole(Role role) {
        log.info("Saving new user to the database", role.getName());
        return roleRepo.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
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
        log.info("New token generated for updated username: " + newToken);


        return newToken;
    }

    public void twoFactors(String twoFactors){
        User user = getAuthenticatedUser();
        if (user == null) {
            throw new UsernameNotFoundException("Authenticated user not found");
        }
        user.setTwoFactorEnabled(Optional.ofNullable(twoFactors)
                .map(Boolean::parseBoolean)
                .orElse(false));

        userRepository.save(user);
    }
}
