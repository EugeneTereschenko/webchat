package com.example.webchat.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "\"messages\"")
@Data
@RequiredArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long messageID;

    @Column(name = "\"user\"")
    private String user;

    private String message;

    @ManyToOne
    @JoinColumn(name = "chat_id")
    private Chat chat;

}
