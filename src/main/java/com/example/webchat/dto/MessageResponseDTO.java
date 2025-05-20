package com.example.webchat.dto;

import lombok.Data;

@Data
public class MessageResponseDTO {
    String username;
    String message;
    String time;
    String avatar;
}
