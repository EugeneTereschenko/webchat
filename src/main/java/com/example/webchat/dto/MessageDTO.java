package com.example.webchat.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
public class MessageDTO {
    private String user;
    private String message;
}
