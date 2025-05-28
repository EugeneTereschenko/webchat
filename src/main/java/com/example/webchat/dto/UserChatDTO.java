package com.example.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserChatDTO {
    private String userId;
    private String username;
    private String message;
    private String time;
    private byte[] avatar;
    private String unreadCount;

    private UserChatDTO(Builder builder) {
        this.userId = builder.userId;
        this.username = builder.username;
        this.message = builder.message;
        this.time = builder.time;
        this.avatar = builder.avatar;
        this.unreadCount = builder.unreadCount;
    }

    public static class Builder {
        private String userId;
        private String username;
        private String message;
        private String time;
        private byte[] avatar;
        private String unreadCount;

        public Builder userId(String userId) {
            this.userId = userId;
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

        public Builder avatar(byte[] avatar) {
            this.avatar = avatar;
            return this;
        }

        public Builder unreadCount(String unreadCount) {
            this.unreadCount = unreadCount;
            return this;
        }

        public UserChatDTO build() {
            return new UserChatDTO(this);
        }
    }
}
