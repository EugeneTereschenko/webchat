package com.example.webchat.service;

import com.example.webchat.converters.ChatToChatDtoConverter;
import com.example.webchat.dto.ChatDTO;
import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageResponseDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.repository.ChatUsersRepository;
import com.example.webchat.service.impl.ActivityService;
import com.example.webchat.service.impl.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private ImageService imageService;

    @Mock
    private UserService userService;

    @Mock
    private ActivityService activityService;

    @Mock
    private ChatUsersRepository chatUsersRepository;

    @Mock
    private ChatToChatDtoConverter chatToChatDtoConverter; // Mock the converter

    @InjectMocks
    private ChatServiceImpl chatServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrCheckChat_ExistingChat() {
        String chatName = "TestChat";
        User mockUser = new User();
        mockUser.setUserID(1L);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        Chat existingChat = new Chat();
        existingChat.setId(1L);
        existingChat.setChatName(chatName);
        existingChat.setUsers(new ArrayList<>());
        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.of(existingChat));

        ChatDTO mockChatDTO = new ChatDTO.Builder()
                .id(existingChat.getId())
                .chatName(existingChat.getChatName())
                .time("10:00 AM")
                .message("Welcome")
                .build();
        when(chatToChatDtoConverter.convertToDto(existingChat.getId(), chatName, existingChat.getUsers()))
                .thenReturn(Optional.of(mockChatDTO));

        Optional<ChatDTO> result = chatServiceImpl.createOrCheckChat(chatName);

        assertTrue(result.isPresent());
        verify(activityService, times(1)).addActivity(eq("Open Chat"), eq(mockUser.getUserID()), any(Date.class));
        verify(chatRepository, never()).save(any(Chat.class));
    }

    @Test
    void testCreateOrCheckChat_NewChat() {
        String chatName = "NewChat";
        User mockUser = new User();
        mockUser.setUserID(1L);
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.empty());

        Chat newChat = new Chat();
        newChat.setId(2L);
        newChat.setChatName(chatName);
        newChat.setUsers(new ArrayList<>());

        when(chatRepository.save(any(Chat.class))).thenReturn(newChat); // Mock saving the chat

        ChatDTO mockChatDTO = new ChatDTO.Builder()
                .id(newChat.getId())
                .chatName(newChat.getChatName())
                .time("10:00 AM")
                .message("Welcome")
                .build();
        when(chatToChatDtoConverter.convertToDto(newChat.getId(), chatName, newChat.getUsers()))
                .thenReturn(Optional.of(mockChatDTO)); // Mock the converter

        Optional<ChatDTO> result = chatServiceImpl.createOrCheckChat(chatName);

        assertTrue(result.isPresent(), "Expected the result to be present, but it was empty.");
        verify(activityService, times(1)).addActivity(eq("Create Chat"), eq(mockUser.getUserID()), any(Date.class));
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void testGetNewChatMessages() {
        String chatName = "TestChat";
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3ZXIiLCJpYXQiOjE2ODAwMDAwMDB9.Av1G07kSLzxyz4nZj0bUYog9Bs8DBHoH7jHPDKtaK20";

        Chat mockChat = new Chat();
        mockChat.setId(1L);
        mockChat.setChatName(chatName);

        Message mockMessage = new Message();
        mockMessage.setId(1L);
        mockMessage.setMessage("Hello");
        mockMessage.setUser("User1");
        mockMessage.setUsersToken(new ArrayList<>());
        mockMessage.setTime(new Date()); // Ensure a valid Date is set

        User mockUser = new User();
        mockUser.setUserID(1L); // Set a valid user ID
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.of(mockChat)); // Ensure a non-empty Optional
        when(messageService.getMessagesByChatId(mockChat.getId())).thenReturn(List.of(mockMessage));

        List<MessageResponseDTO> result = chatServiceImpl.getNewChatMessages(chatName, token);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("User1", result.get(0).getUsername());
        assertEquals("Hello", result.get(0).getMessage());
        verify(messageService, times(1)).updateMessage(mockMessage);
    }

    @Test
    void testGetOldChatMessages() {
        String chatName = "TestChat";
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3ZXIiLCJpYXQiOjE2ODAwMDAwMDB9.Av1G07kSLzxyz4nZj0bUYog9Bs8DBHoH7jHPDKtaK20";
        String userToken = "Av1G07kSLzxyz4nZj0bUYog9Bs8DBHoH7jHPDKtaK20";

        Chat mockChat = new Chat();
        mockChat.setId(1L);
        mockChat.setChatName(chatName);

        Message mockMessage = new Message();
        mockMessage.setId(1L);
        mockMessage.setMessage("Hello");
        mockMessage.setUser("User1");
        mockMessage.setUsersToken(List.of(userToken));
        mockMessage.setTime(new Date()); // Ensure a valid Date is set

        User mockUser = new User();
        mockUser.setUserID(1L); // Set a valid user ID
        when(userService.getAuthenticatedUser()).thenReturn(mockUser);

        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.of(mockChat)); // Ensure a non-empty Optional

        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.of(mockChat));
        when(messageService.getMessagesByChatId(mockChat.getId())).thenReturn(List.of(mockMessage));

        List<MessageResponseDTO> result = chatServiceImpl.getOldChatMessages(chatName, token);

        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).getMessage());
        assertEquals("User1", result.get(0).getUsername());
        verify(chatRepository, times(1)).findByChatName(chatName);
        verify(messageService, times(1)).getMessagesByChatId(mockChat.getId());
    }
}