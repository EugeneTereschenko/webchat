package com.example.webchat.service;

import com.example.webchat.dto.CardDTO;
import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.model.Card;
import com.example.webchat.model.Profile;
import com.example.webchat.model.User;
import com.example.webchat.repository.CardRepository;
import com.example.webchat.repository.ProfileRepository;
import com.example.webchat.service.impl.CardService;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Optional;

@AllArgsConstructor
@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ProfileRepository profileRepository;

    @Override
    public Optional<CardDTO> createCard(CardDTO cardDTO) {
    // Validate the cardDTO before creating a new Card
        if (cardDTO == null || cardDTO.getCardExpiryDate() == null || cardDTO.getCardNumber() == null) {
            return Optional.empty(); // Return empty if the DTO is invalid
        }
        // Create a new Card object from the DTO
        Card card = new Card.Builder()
                .cardNumber(cardDTO.getCardNumber())
                .cardExpiryDate(cardDTO.getCardExpiryDate())
                .cardType(cardDTO.getCardType())
                .nameOfCard(cardDTO.getNameOfCard())
                .cvv(cardDTO.getCvv())
                .build();
        // Save the card to the repository
        Card savedCard = cardRepository.save(card);

        Profile profile = profileRepository.findById(Long.valueOf(cardDTO.getProfileId()))
                .orElseThrow(() -> new IllegalArgumentException("Profile with ID " + cardDTO.getProfileId() + " does not exist."));

        profile.setCard(card);
        profileRepository.save(profile);

        // Check if the card was saved successfully
        if (savedCard == null) {
            return Optional.empty(); // Return empty if saving failed
        }
        // Convert the saved Card back to CardDTO
        CardDTO savedCardDTO = new CardDTO.Builder()
                .id(savedCard.getId())
                .cardNumber(savedCard.getCardNumber())
                .cardExpiryDate(savedCard.getCardExpiryDate())
                .cardType(savedCard.getCardType())
                .nameOfCard(savedCard.getNameOfCard())
                .cvv(savedCard.getCvv())
                .build();

        return Optional.of(savedCardDTO);
    }

    @Override
    public Optional<Card> getCardById(Long cardId) {
        return cardRepository.findById(cardId)
                .map(Optional::of)
                .orElseGet(() -> Optional.empty());
    }


    @Override
    public Optional<Card> updateCard(Long cardId, Card card) {
        return Optional.empty();
    }

    @Override
    public Optional<Card> updateCard(CardDTO cardDTO) {

        if (cardDTO == null || cardDTO.getCardNumber() == null || cardDTO.getCardExpiryDate() == null) {
            return Optional.empty();
        }

        Card existingCard = cardRepository.findById(cardDTO.getId())
                .orElseThrow(() -> new IllegalArgumentException("Card with ID " + cardDTO.getId() + " does not exist."));

        existingCard.setCardNumber(cardDTO.getCardNumber());
        existingCard.setCardExpiryDate(cardDTO.getCardExpiryDate());
        existingCard.setCardType(cardDTO.getCardType());
        existingCard.setNameOfCard(cardDTO.getNameOfCard());

        Card updatedCard = cardRepository.save(existingCard);
        if (updatedCard != null) {
            return Optional.of(updatedCard);
        }
        return Optional.empty();
    }

    @Override
    public void deleteCard(Long cardId) {
        // Check if the card exists before attempting to delete
        Card existingCard = cardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card with ID " + cardId + " does not exist."));
        cardRepository.delete(existingCard);
    }
}
