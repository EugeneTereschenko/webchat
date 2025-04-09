package com.example.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@ToString
@Getter
@Setter
public class MessageChatDTO {
    private String user;
    private String message;
    private String chatName;
}
