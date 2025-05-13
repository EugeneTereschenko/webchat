package com.example.webchat.service;

import com.example.webchat.dto.UserDTO;
import com.example.webchat.dto.UserResponseDTO;
import com.example.webchat.model.Role;
import com.example.webchat.model.User;
import com.example.webchat.repository.UserRepository;
import com.example.webchat.repository.RoleRepository;
import com.example.webchat.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private TwoFactorAuthService twoFactorAuthService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private ActivityServiceImpl activityService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepo;

    @Mock
    private EmailNotificationService emailService;

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Mock
    private AuthenticationManager authenticationManager;

    private User user;
    private UserDTO userDTO;

    @Mock
    private UserDetails userDetails;

    @Mock
    private UserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword123");
        user.setUserID(1L);

        userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("password123");
        userDTO.setUserCode("123456"); // Set a valid user code

        userDetails = org.springframework.security.core.userdetails.User
                .withUsername("testUser")
                .password("encodedPassword123")
                .roles("USER")
                .build();
    }

    @Test
    void verifyOtpAndLogin_validOtp() {
        when(twoFactorAuthService.verifyCode(user.getSecretKey(), Integer.parseInt(userDTO.getUserCode()))).thenReturn(true);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("testToken");

        UserResponseDTO response = userService.verifyOtpAndLogin(user, userDTO);

        assertNotNull(response);
        assertEquals("true", response.getSuccess());
        assertEquals("OTP verified successfully", response.getMessage());
        assertEquals("testToken", response.getToken());
        assertEquals("1", response.getUserID());
        verify(activityService, times(1)).addActivity(eq("User login"), eq(1L), any());
    }

    @Test
    void verifyOtpAndLogin_invalidOtp() {
        when(twoFactorAuthService.verifyCode(user.getSecretKey(), Integer.parseInt(userDTO.getUserCode()))).thenReturn(false);

        UserResponseDTO response = userService.verifyOtpAndLogin(user, userDTO);

        assertNotNull(response);
        assertEquals("false", response.getSuccess());
        assertEquals("Invalid OTP", response.getMessage());
        assertNull(response.getToken());
        verify(activityService, never()).addActivity(anyString(), anyLong(), any());
    }

    @Test
    void verifyOtpAndLogin_exceptionHandling() {
        when(twoFactorAuthService.verifyCode(anyString(), anyInt())).thenThrow(new RuntimeException("Test exception"));

        UserResponseDTO response = userService.verifyOtpAndLogin(user, userDTO);

        assertNotNull(response);
        assertEquals("false", response.getSuccess());
        assertNull(response.getToken());
        assertEquals("Invalid OTP", response.getMessage());
        verify(activityService, never()).addActivity(anyString(), anyLong(), any());
    }

    @Test
    void registerAndAddRole() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setEmail("test@example.com");
        userDTO.setPassword("password123");

        User user = new User();
        user.setUsername("testUser");
        user.setEmail("test@example.com");
        user.setUserID(1L);
        user.setRoles(new ArrayList<>()); // Initialize roles to avoid null

        Role role = new Role();
        role.setName("ROLE_USER");

        when(userRepository.findByUsername(userDTO.getUsername())).thenReturn(null);
        when(passwordEncoder.encode(userDTO.getPassword())).thenReturn("encodedPassword123");
        when(userRepository.save(any(User.class))).thenReturn(user); // Ensure user is returned
        when(roleRepo.findByName("ROLE_USER")).thenReturn(null);
        when(roleRepo.save(any(Role.class))).thenReturn(role);

        UserResponseDTO response = userService.registerAndAddRole(userDTO);

        assertNotNull(response);
        assertEquals("true", response.getSuccess());
        assertEquals("User registered successfully", response.getMessage());
        verify(passwordEncoder, times(1)).encode(userDTO.getPassword());
        verify(userRepository, times(1)).save(any(User.class));
        verify(roleRepo, times(1)).save(any(Role.class));
        verify(userRepository, times(1)).findByUsername(userDTO.getUsername());
        verify(roleRepo, times(1)).findByName("ROLE_USER");
        verify(activityService, times(1)).addActivity(eq("User registered"), eq(1L), any());
    }

    @Test
    void loginUser() {
        UserDTO userDTO = new UserDTO();
        userDTO.setUsername("testUser");
        userDTO.setPassword("password123");

        User user = new User();
        user.setUsername("testUser");
        user.setPassword("encodedPassword123");
        user.setUserID(1L);

        Authentication authentication = mock(Authentication.class);

        when(userDetailsService.loadUserByUsername(userDTO.getUsername())).thenReturn(userDetails);
        when(passwordEncoder.matches(userDTO.getPassword(), userDetails.getPassword())).thenReturn(true);
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
        when(jwtUtil.generateToken(user.getUsername())).thenReturn("testToken");

        UserResponseDTO response = userService.loginUser(userDTO, user);

        assertNotNull(response);
        assertEquals("true", response.getSuccess());
        assertEquals("User logged in successfully", response.getMessage());
        assertEquals("testToken", response.getToken());
        verify(activityService, times(1)).addActivity(eq("User login"), eq(1L), any());
    }

    @Test
    void checkAuth_withTwoFactorEnabled() throws Exception {
        // Arrange
        user.setTwoFactorEnabled(true);
        when(twoFactorAuthService.generateCode(user.getSecretKey())).thenReturn("123456");
        doNothing().when(emailService).sendEmailWithAttachment(anyString(), anyString(), anyString(), anyString());

        // Act
        UserResponseDTO response = userService.checkAuth(user);

        // Assert
        assertNotNull(response);
        assertEquals("true", response.getSuccess());
        assertEquals("true", response.getTwofactor());
        assertEquals("Two-factor code sent to your email", response.getMessage());
        verify(twoFactorAuthService, times(1)).generateCode(user.getSecretKey());
        verify(emailService, times(1)).sendEmailWithAttachment(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void checkAuth_withoutTwoFactorEnabled() throws Exception {
        // Arrange
        user.setTwoFactorEnabled(false);

        // Act
        UserResponseDTO response = userService.checkAuth(user);

        // Assert
        assertNotNull(response);
        assertEquals("true", response.getSuccess());
        assertEquals("false", response.getTwofactor());
        assertEquals("User is authenticated by default", response.getMessage());
        verify(twoFactorAuthService, never()).generateCode(anyString());
        verify(emailService, never()).sendEmailWithAttachment(anyString(), anyString(), anyString(), anyString());
    }
}