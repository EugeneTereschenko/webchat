package com.example.webchat.dto;

import lombok.Data;

@Data
public class UserResponseDTO {
    private String twofactor;
    private String message;
    private String success;
    private String userID;
    private String token;

    public UserResponseDTO(){

    }

    public UserResponseDTO(String message, String success) {
        this.message = message;
        this.success = success;
    }
}
