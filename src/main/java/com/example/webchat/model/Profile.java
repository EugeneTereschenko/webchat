package com.example.webchat.model;

import com.example.webchat.dto.ProfileDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@Entity
@Table(name = "\"profile\"")
@NoArgsConstructor
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

    @Column(name = "notification", length = 255)
    private Boolean notification;

    @Column(name = "card_type", length = 255)
    private String cardType;

    @Column(name = "name_of_card", length = 255)
    private String nameOfCard;

    @Column(name = "card_number", length = 255)
    private String cardNumber;

    @Column(name = "expiration_date", length = 255)
    private String cardExpiryDate;

    @Column(name = "cvv", length = 255)
    private String cvv;


    private Profile(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.phoneNumber = builder.phoneNumber;
        this.address = builder.address;
        this.shippingAddress = builder.shippingAddress;
        this.staff = builder.staff;
        this.bio = builder.bio;
        this.profilePicture = builder.profilePicture;
        this.notification = builder.notification;
        this.cardType = builder.cardType;
        this.nameOfCard = builder.nameOfCard;
        this.cardNumber = builder.cardNumber;
        this.cardExpiryDate = builder.cardExpiryDate;
        this.cvv = builder.cvv;
    }


    public static class Builder {
        private Long id;
        private Long userId;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private String address;
        private String shippingAddress;
        private String staff;
        private String bio;
        private String profilePicture;
        private Boolean notification;
        private String cardType;
        private String nameOfCard;
        private String cardNumber;
        private String cardExpiryDate;
        private String cvv;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            this.phoneNumber = phoneNumber;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder shippingAddress(String shippingAddress) {
            this.shippingAddress = shippingAddress;
            return this;
        }

        public Builder staff(String staff) {
            this.staff = staff;
            return this;
        }

        public Builder bio(String bio) {
            this.bio = bio;
            return this;
        }

        public Builder profilePicture(String profilePicture) {
            this.profilePicture = profilePicture;
            return this;
        }

        public Builder notification(Boolean notification) {
            this.notification = notification;
            return this;
        }

        public Builder cardType(String cardType) {
            this.cardType = cardType;
            return this;
        }

        public Builder nameOfCard(String nameOfCard) {
            this.nameOfCard = nameOfCard;
            return this;
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder cardExpiryDate(String cardExpiryDate) {
            this.cardExpiryDate = cardExpiryDate;
            return this;
        }

        public Builder cvv(String cvv) {
            this.cvv = cvv;
            return this;
        }

        public Profile build() {
            return new Profile(this);
        }
    }

}
