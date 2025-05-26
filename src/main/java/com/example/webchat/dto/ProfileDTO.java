package com.example.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProfileDTO {

    private String firstName;

    private String lastName;

    private String username;

    private String email;

    private String phoneNumber;

    private String address;

    private String shippingAddress;

    private String staff;

    private String bio;

    private String isActive;

    private String notification;

    private String twoFactors;

    private String message;




}
