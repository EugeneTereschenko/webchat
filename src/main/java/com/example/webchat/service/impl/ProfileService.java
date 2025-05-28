package com.example.webchat.service.impl;

import com.example.webchat.dto.CardDTO;
import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.dto.ProfileResponseDTO;
import com.example.webchat.model.Profile;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public interface ProfileService {
    void saveProfile(ProfileDTO profileDTO);
    Optional<ProfileResponseDTO> updateProfile(ProfileDTO profileDTO);
    Optional<ProfileResponseDTO> createProfile(ProfileDTO profileDTO);
    Optional<ProfileDTO> getProfileByUserId(Long userId);
    Optional<Profile> getProfileByUserId();
    List<ProfileDTO> getAllProfiles();
    boolean addCardToProfile(Long cardId);
    Optional<CardDTO> createAndAddCardToProfile(CardDTO cardDTO);
    boolean updateNotification(String name, Boolean notification);
    Optional<CardDTO> getCard();
    Optional<ProfileDTO> getProfile();
    Optional<ProfileResponseDTO> getUpdateNotification(String notification);
    Optional<ProfileResponseDTO> getUpdateMessage(String message);
    HashMap<String, String> getActivityByUser(String numOfLogs);

}
