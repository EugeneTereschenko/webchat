package com.example.webchat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Entity
@Table(name = "\"chat\"")
@RequiredArgsConstructor
@Data
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="chat_name", length = 255, nullable = false)
    private String chatName;

    @Column(name="users", length = 255)
    private List<String> users;

    public static class Builder {
        private Long id;
        private String chatName;
        private List<String> users;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder chatName(String chatName) {
            this.chatName = chatName;
            return this;
        }

        public Builder users(List<String> users) {
            this.users = users;
            return this;
        }

        public Chat build() {
            Chat chat = new Chat();
            chat.id = this.id;
            chat.chatName = this.chatName;
            chat.users = this.users;
            return chat;
        }
    }
}
