package com.example.webchat.dto;

import lombok.Data;

@Data
public class UserChatDTO {
    String username;
    String message;
    String time;
    String avatar;
    String unreadCount;
}
