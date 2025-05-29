package com.example.webchat.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "images")
@Data
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long userId;

    @Lob
    @Column(nullable = false)
    private byte[] data;

    public static class Builder {
        private Long id;
        private String name;
        private Long userId;
        private byte[] data;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder userId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder data(byte[] data) {
            this.data = data;
            return this;
        }

        public Image build() {
            Image image = new Image();
            image.id = this.id;
            image.name = this.name;
            image.userId = this.userId;
            image.data = this.data;
            return image;
        }
    }
}
