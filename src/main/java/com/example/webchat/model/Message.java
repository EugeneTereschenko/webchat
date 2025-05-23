package com.example.webchat.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Table(name = "\"message\"")
@AllArgsConstructor
@Entity
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"user\"")
    private String user;

    private String message;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

    private Boolean isRead = false;

    @Column(name="usersRead", length = 255)
    private List<String> usersRead;

    @Column(name = "\"usersToken\"", length = 255)
    private List<String> usersToken;

    @Column(name = "\"time\"")
    private Date time;

    public Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public Message() {}

    private Message(Builder builder) {
        this.id = builder.id;
        this.user = builder.user;
        this.message = builder.message;
        this.chat = builder.chat;
        this.isRead = builder.isRead;
        this.usersRead = builder.usersRead;
        this.usersToken = builder.usersToken;
        this.time = builder.time;
    }

    public static class Builder {
        private Long id;
        private String user;
        private String message;
        private Chat chat;
        private Boolean isRead = false;
        private List<String> usersRead;
        private List<String> usersToken;
        private Date time;

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

        public Builder chat(Chat chat) {
            this.chat = chat;
            return this;
        }

        public Builder isRead(Boolean isRead) {
            this.isRead = isRead;
            return this;
        }

        public Builder usersRead(List<String> usersRead) {
            this.usersRead = usersRead;
            return this;
        }

        public Builder usersToken(List<String> usersToken) {
            this.usersToken = usersToken;
            return this;
        }

        public Builder time(Date time) {
            this.time = time;
            return this;
        }

        public Message build() {
            return new Message(this);
        }
    }
}