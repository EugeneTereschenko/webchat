package com.example.webchat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "\"chat_users\"")
@RequiredArgsConstructor
@Data
public class ChatUsers {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chat_id", nullable = false)
    private Long chatId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "\"time\"")
    private Date time;

    private ChatUsers(Builder builder) {
        this.id = builder.id;
        this.chatId = builder.chatId;
        this.userId = builder.userId;
        this.time = builder.time;
    }

    public static class Builder {
        private Long id;
        private Long chatId;
        private Long userId;
        private Date time;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder time(Date time) {
            this.time = time;
            return this;
        }

        public ChatUsers build() {
            return new ChatUsers(this);
        }
    }

}
