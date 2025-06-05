package com.example.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class ChatDTO {
    private Long id;
    private String chatName;
    private String time;
    private String message;
    private List<String> users;

    public ChatDTO(Long id, String chatName) {
        this.id = id;
        this.chatName = chatName;
    }


    private ChatDTO(Long id, String chatName, String time, String message, List<String> users) {
        this.id = id;
        this.chatName = chatName;
        this.time = time;
        this.message = message;
        this.users = users;
    }

    public static class Builder {
        private Long id;
        private String chatName;
        private String time;
        private String message;
        private List<String> users;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chatName(String chatName) {
            this.chatName = chatName;
            return this;
        }

        public Builder time(String time) {
            this.time = time;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder users(List<String> users) {
            this.users = users;
            return this;
        }

        public ChatDTO build() {
            return new ChatDTO(id, chatName, time, message, users);
        }
    }

}
