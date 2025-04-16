package com.example.webchat.service;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.model.Profile;
import com.example.webchat.repository.ProfileRepository;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;

import java.util.Optional;

@AllArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;

    public void saveProfile(ProfileDTO profileDTO) {
        Profile profile = new Profile();

        profile.setFirstName(Optional.ofNullable(profileDTO.getFirstName()).orElse("John"));
        profile.setLastName(Optional.ofNullable(profileDTO.getLastName()).orElse("Doe"));
        profile.setShippingAddress(Optional.ofNullable(profileDTO.getShippingAddress()).orElse("123 Main St, Springfield, USA"));
        profile.setStaff(Optional.ofNullable(profileDTO.getStaff()).orElse("Junior"));
        profile.setBio(Optional.ofNullable(profileDTO.getBio()).orElse("Passionate about solving complex problems through simple and elegant designs."));
        profile.setAddress(Optional.ofNullable(profileDTO.getAddress()).orElse("123 Main St, Springfield, USA"));
        profile.setPhoneNumber(Optional.ofNullable(profileDTO.getPhoneNumber()).orElse("123-456-7890"));
        profile.setProfilePicture(Optional.ofNullable(profileDTO.getProfilePicture()).orElse("default.jpg"));
        profile.setCardType(Optional.ofNullable(profileDTO.getCardType()).orElse("Visa"));
        profile.setCardNumber(Optional.ofNullable(profileDTO.getCardNumber()).orElse("1234-5678-9012-3456"));
        profile.setCardExpiryDate(Optional.ofNullable(profileDTO.getCardExpiryDate()).orElse("12/25"));
        profile.setCVV(Optional.ofNullable(profileDTO.getCVV()).orElse("123"));

        profileRepository.save(profile);
    }


    public Optional<Profile> createProfile(ProfileDTO profileDTO){

        return Optional.empty();
    }
}
