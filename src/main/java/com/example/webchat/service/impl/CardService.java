package com.example.webchat.service.impl;

import com.example.webchat.dto.CardDTO;
import com.example.webchat.model.Card;

import java.util.Optional;

public interface CardService {
    Optional<CardDTO> createCard(CardDTO cardDTO);
    Optional<Card> getCardById(Long cardId);
    Optional<Card> updateCard(Long cardId, Card card);
    Optional<Card> updateCard(CardDTO cardDTO);
    void deleteCard(Long cardId);
}
