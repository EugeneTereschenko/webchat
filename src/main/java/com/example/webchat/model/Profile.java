package com.example.webchat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "\"profile\"")
@RequiredArgsConstructor
@Data
public class Profile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", length = 255)
    private Long userId;

    @Column(name = "firstname", length = 255)
    private String firstName;

    @Column(name = "lastname", length = 255)
    private String lastName;

    @Column(name = "phone", length = 255)
    private String phoneNumber;

    @Column(name = "address", length = 255)
    private String address;

    @Column(name = "shippingAddress", length = 255)
    private String shippingAddress;

    @Column(name = "staff", length = 255)
    private String staff;

    @Column(name = "bio", length = 255)
    private String bio;

    @Column(name = "profile_picture", length = 255)
    private String profilePicture;

    @Column(name = "card_type", length = 255)
    private String cardType;

    @Column(name = "name_of_card", length = 255)
    private String NameOfCard;

    @Column(name = "card_number", length = 255)
    private String CardNumber;

    @Column(name = "expiration_date", length = 255)
    private String CardExpiryDate;

    @Column(name = "cvv", length = 255)
    private String CVV;

}
