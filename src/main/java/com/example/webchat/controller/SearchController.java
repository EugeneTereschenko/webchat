package com.example.webchat.controller;

import com.example.webchat.dto.ChatDTO;
import com.example.webchat.dto.MessageChatDTO;
import com.example.webchat.service.SearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/api/search/chats")
    public ResponseEntity<List<ChatDTO>> searchChats(@RequestParam String keyword) {
        log.debug(" Searching chat with keyword: {}", keyword);
        return ResponseEntity.ok().body(searchService.searchChats(keyword));
    }

    @GetMapping("/api/search/messages")
    public ResponseEntity<List<MessageChatDTO>> searchMessages(@RequestParam String keyword) {
        log.debug(" Searching messages with keyword: {}", keyword);
        return ResponseEntity.ok().body(searchService.searchMessages(keyword));
    }
}
