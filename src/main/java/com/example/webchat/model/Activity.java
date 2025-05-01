package com.example.webchat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "\"Activity\"")
@RequiredArgsConstructor
@Data
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String activity;
    private Date timestamp;

    private Activity(Builder builder) {
        this.id = builder.id;
        this.userId = builder.userId;
        this.activity = builder.activity;
        this.timestamp = builder.timestamp;
    }

    public static class Builder {
        private Long id;
        private Long userId;
        private String activity;
        private Date timestamp;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder activity(String activity) {
            this.activity = activity;
            return this;
        }

        public Builder activityDate(Date timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Activity build() {
            return new Activity(this);
        }
    }
}