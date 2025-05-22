package com.example.webchat.service;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.dto.MessageResponseDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.repository.ChatRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private MessageService messageService;

    @Mock
    private UserService userService;

    @Mock
    private ActivityService activityService;

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
        existingChat.setChatName(chatName);
        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.of(existingChat));

        Optional<Chat> result = chatServiceImpl.createOrCheckChat(chatName);

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

        Optional<Chat> result = chatServiceImpl.createOrCheckChat(chatName);

        assertTrue(result.isPresent());
        verify(activityService, times(1)).addActivity(eq("Create Chat"), eq(mockUser.getUserID()), any(Date.class));
        verify(chatRepository, times(1)).save(any(Chat.class));
    }

    @Test
    void testGetNewChatMessages() {
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
        mockMessage.setUsersToken(new ArrayList<>());

        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.of(mockChat));
        when(messageService.getMessagesByChatId(mockChat.getId())).thenReturn(List.of(mockMessage));

        List<MessageResponseDTO> result = chatServiceImpl.getNewChatMessages(chatName, token);

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

        when(chatRepository.findByChatName(chatName)).thenReturn(Optional.of(mockChat));
        when(messageService.getMessagesByChatId(mockChat.getId())).thenReturn(List.of(mockMessage));

        List<MessageChatDTO> result = chatServiceImpl.getOldChatMessages(chatName, token);

        assertEquals(1, result.size());
        assertEquals("Hello", result.get(0).getMessage());
        assertEquals("User1", result.get(0).getUser());
        verify(chatRepository, times(1)).findByChatName(chatName);
        verify(messageService, times(1)).getMessagesByChatId(mockChat.getId());
    }
}