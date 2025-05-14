package com.example.webchat.controller;

import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.service.SearchService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @GetMapping("/search/chats")
    public List<Chat> searchChats(@RequestParam String keyword) {
        return searchService.searchChats(keyword);
    }

    @GetMapping("/search/messages")
    public List<Message> searchMessages(@RequestParam String keyword) {
        log.info(" Searching messages with keyword: {}", keyword);
        return searchService.searchMessages(keyword);
    }
}
