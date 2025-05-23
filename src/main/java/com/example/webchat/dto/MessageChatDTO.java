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

    private MessageChatDTO(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.message = builder.message;
        this.chatName = builder.chatName;
    }

    public static class Builder {
        private Long id;
        private String user;
        private String message;
        private String chatName;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder user(String user) {
            this.user = user;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder chatName(String chatName) {
            this.chatName = chatName;
            return this;
        }

        public MessageChatDTO build() {
            return new MessageChatDTO(this);
        }
    }
}
