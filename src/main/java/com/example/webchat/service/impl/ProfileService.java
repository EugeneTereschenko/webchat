package com.example.webchat.service.impl;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.model.Profile;

import java.util.List;
import java.util.Optional;

public interface ProfileService {
    boolean updateNotification(String name, Boolean notification);
    void saveProfile(ProfileDTO profileDTO);
    void updateProfile(ProfileDTO profileDTO);
    Optional<Profile> createProfile(ProfileDTO profileDTO);
    Optional<ProfileDTO> getProfileByUserId(Long userId);
    List<ProfileDTO> getAllProfiles(Long userId);
}
