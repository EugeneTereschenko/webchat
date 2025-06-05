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
    private String userId;
    private String email;
    private String username;
    private String password;
    private String newPassword;
    private String isActive;
    private String userCode;
    private String staff;
    private String bio;


    // Default constructor
    public UserDTO() {}

    // Parameterized constructor
    public UserDTO(String userId, String email, String username, String password, String newPassword, String isActive, String userCode, String staff, String bio) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.password = password;
        this.newPassword = newPassword;
        this.isActive = isActive;
        this.userCode = userCode;
        this.staff = staff;
        this.bio = bio;
    }

    // Builder pattern for UserDTO
    public static class Builder {
        private String userId;
        private String email;
        private String username;
        private String password;
        private String newPassword;
        private String isActive;
        private String userCode;
        private String staff;
        private String bio;

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder username(String username) {
            this.username = username;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder newPassword(String newPassword) {
            this.newPassword = newPassword;
            return this;
        }

        public Builder isActive(String isActive) {
            this.isActive = isActive;
            return this;
        }

        public Builder userCode(String userCode) {
            this.userCode = userCode;
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

        public UserDTO build() {
            return new UserDTO(userId, email, username, password, newPassword, isActive, userCode, staff, bio);
        }
    }
}
