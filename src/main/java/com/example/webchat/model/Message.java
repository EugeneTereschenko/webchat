package com.example.webchat.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "\"messages\"")
@Data

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

    public Message(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public Message() {
    }
}
