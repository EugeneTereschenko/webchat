package com.example.webchat.controller;


import com.example.webchat.dto.CardDTO;
import com.example.webchat.service.impl.CardService;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Controller
public class CardController {

    private final CardService cardService;
    private final ProfileService profileService;

    @PostMapping("/api/sendCard")
    public ResponseEntity<?> sendCard(@Valid @RequestBody CardDTO cardDTO) {
        log.info("Sending card: {}", cardDTO);
        Optional<CardDTO> cardSaveDTO = cardService.createCard(cardDTO);

        if (cardSaveDTO.isPresent()) {
            return ResponseEntity.ok(cardSaveDTO.get());
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create card");
        }
    }

    @PutMapping("/api/updateCard")
    public ResponseEntity<?> updateCard(@Valid @RequestBody CardDTO cardDTO) {
        log.info("Updating card: {}", cardDTO);
        Optional<CardDTO> updatedCard = profileService.createAndAddCardToProfile(cardDTO);

        if (updatedCard.isPresent()) {
            return ResponseEntity.ok(updatedCard.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found");
        }
    }

    @GetMapping("/api/getCard")
    public ResponseEntity<?> getCard() {
        Optional<CardDTO> card = profileService.getCard();
        log.info("Retrieving card: {}", card);
        if (card.isPresent()) {
            return ResponseEntity.ok(card.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Card not found");
        }
    }
}
