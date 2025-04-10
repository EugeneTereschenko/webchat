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

    @Column(name="chat_name", length = 255)
    private String chatName;

    @Column(name="users", length = 255)
    private List<String> users;
}
