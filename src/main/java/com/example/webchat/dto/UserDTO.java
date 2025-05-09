package com.example.webchat.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.Email;

@ToString
@Getter
@Setter
public class UserDTO {

    @Email(message = "Email is not valid")
    private String email;
    private String username;
    private String password;
    private String newPassword;
    private String isActive;
    private String userCode;
    private String staff;
    private String bio;
}
