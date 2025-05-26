package com.example.webchat.dto;

import lombok.Data;

@Data
public class ProfileResponseDTO {
    private String profileId;
    private String message;
    private String success;
}
