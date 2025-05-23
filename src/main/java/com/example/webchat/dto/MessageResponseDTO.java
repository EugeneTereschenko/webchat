package com.example.webchat.dto;

import lombok.Data;

@Data
public class MessageResponseDTO {
    private Long id;
    private String username;
    private String message;
    private String time;
    private Boolean isRead;
    private byte[] avatar;

    private MessageResponseDTO(Builder builder) {
        this.id = builder.id;
        this.username = builder.username;
        this.message = builder.message;
        this.time = builder.time;
        this.isRead = builder.isRead;
        this.avatar = builder.avatar;
    }

    public static class Builder {
        private Long id;
        private String username;
        private String message;
        private String time;
        private Boolean isRead;
        private byte[] avatar;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder time(String time) {
            this.time = time;
            return this;
        }

        public Builder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Builder avatar(byte[] avatar) {
            this.avatar = avatar;
            return this;
        }

        public MessageResponseDTO build() {
            return new MessageResponseDTO(this);
        }
    }
}
