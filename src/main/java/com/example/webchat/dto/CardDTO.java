package com.example.webchat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CardDTO {
    private Long id;
    private String profileId;
    private String cardType;
    private String nameOfCard;
    private String cardNumber;
    private String cardExpiryDate;
    private String cvv;

    public static class Builder {
        private Long id;
        private String profileId;
        private String cardType;
        private String nameOfCard;
        private String cardNumber;
        private String cardExpiryDate;
        private String cvv;

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder profileId(String profileId) {
            this.profileId = profileId;
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

        public CardDTO build() {
            CardDTO cardDTO = new CardDTO();
            cardDTO.setId(id);
            cardDTO.setProfileId(profileId);
            cardDTO.setCardType(cardType);
            cardDTO.setNameOfCard(nameOfCard);
            cardDTO.setCardNumber(cardNumber);
            cardDTO.setCardExpiryDate(cardExpiryDate);
            cardDTO.setCvv(cvv);
            return cardDTO;
        }
    }
}
