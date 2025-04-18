package com.example.webchat.service.impl;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.model.Profile;

import java.util.Optional;

public interface ProfileService {
    void saveProfile(ProfileDTO profileDTO);
    Optional<Profile> createProfile(ProfileDTO profileDTO);
    Optional<ProfileDTO> getProfileByUserId(Long userId);
}
