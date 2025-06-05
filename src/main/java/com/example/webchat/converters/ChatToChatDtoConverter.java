package com.example.webchat.converters;


import com.example.webchat.dto.ChatDTO;
import com.example.webchat.util.DateTimeConverter;
import com.example.webchat.util.TimeAgoFormatter;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ChatToChatDtoConverter {

    public Optional<ChatDTO> convertToDto(Long id, String chatName, List<String> users) {
        return Optional.of(new ChatDTO.Builder()
                .id(id)
                .chatName(chatName)
                .time(TimeAgoFormatter.timeAgo(DateTimeConverter.toLocalDateTime(new Date())))
                .message("Create Chat successfully")
                .build());
    }
}
