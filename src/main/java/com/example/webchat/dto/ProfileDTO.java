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

    private ProfileDTO(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.username = builder.username;
        this.email = builder.email;
        this.phoneNumber = builder.phoneNumber;
        this.address = builder.address;
        this.shippingAddress = builder.shippingAddress;
        this.staff = builder.staff;
        this.bio = builder.bio;
        this.isActive = builder.isActive;
        this.notification = builder.notification;
        this.twoFactors = builder.twoFactors;
        this.message = builder.message;
    }

    public static class Builder {
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

        public Builder firstName(String firstName) {
            this.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            this.lastName = lastName;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
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

        public Builder isActive(String isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder notification(String notification) {
            this.notification = notification;
            return this;
        }

        public Builder twoFactors(String twoFactors) {
            this.twoFactors = twoFactors;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public ProfileDTO build() {
            return new ProfileDTO(this);
        }
    }




}
