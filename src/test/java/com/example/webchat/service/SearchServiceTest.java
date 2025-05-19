package com.example.webchat.service;

import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @InjectMocks
    private SearchService searchService;

    @Mock
    private MessageRepository messageRepository;

    @BeforeEach
    void setUp() {
        // Manually set the limit field
        searchService.setLimit(10L);
    }

    @Test
    void searchMessages() {
        // Arrange
        String keyword = "test";
        String page = "1";

        Chat chat = new Chat();
        chat.setChatName("Test Chat");

        Message message = new Message();
        message.setId(1L);
        message.setUser("User1");
        message.setMessage("Test message");
        message.setChat(chat);

        when(messageRepository.findByMessageContainingIgnoreCase(keyword))
                .thenReturn(List.of(message));

        // Act
        List<MessageChatDTO> result = searchService.searchMessages(keyword, page);

        // Assert
        assertEquals(1, result.size());
        assertEquals("User1", result.get(0).getUser());
        assertEquals("Test message", result.get(0).getMessage());
        assertEquals("Test Chat", result.get(0).getChatName());
    }

    @Test
    void testValueInjection() {
        // Assert that the @Value is correctly injected
        assertEquals(10L, searchService.getLimit());
    }
}