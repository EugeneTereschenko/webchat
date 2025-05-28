package com.example.webchat.dto;

import lombok.Data;

@Data
public class ProfileResponseDTO {
    private String profileId;
    private String message;
    private String success;
    private String token;
    private String userID;

    public ProfileResponseDTO() {
    }

    public ProfileResponseDTO(String message, String success) {
        this.message = message;
        this.success = success;
    }
}
