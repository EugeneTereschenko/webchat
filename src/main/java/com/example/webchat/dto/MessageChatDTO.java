package com.example.webchat.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Getter
@Setter
public class MessageChatDTO {
    private Long id;
    private String user;
    private String message;
    private String chatName;
}
