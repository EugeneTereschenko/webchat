package com.example.webchat.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "\"card\"")
@NoArgsConstructor
@Data
public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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

    // Static nested Builder class
    public static class Builder {
        private Long id;
        private String cardType;
        private String nameOfCard;
        private String cardNumber;
        private String cardExpiryDate;
        private String cvv;

        public Builder id(Long id) {
            this.id = id;
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

        public Card build() {
            Card card = new Card();
            card.id = this.id;
            card.cardType = this.cardType;
            card.nameOfCard = this.nameOfCard;
            card.cardNumber = this.cardNumber;
            card.cardExpiryDate = this.cardExpiryDate;
            card.cvv = this.cvv;
            return card;
        }
    }




}
