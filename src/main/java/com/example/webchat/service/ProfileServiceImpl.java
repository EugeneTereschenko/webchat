package com.example.webchat.service;

import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.exception.ProfileNotValueException;
import com.example.webchat.model.Profile;
import com.example.webchat.model.User;
import com.example.webchat.repository.ProfileRepository;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final UserService userService;
    private final EmailNotificationService emailNotificationService;

    public void saveProfile(ProfileDTO profileDTO) {
        Profile profile = new Profile.Builder()
                .firstName(Optional.ofNullable(profileDTO.getFirstName()).orElse("John"))
                .lastName(Optional.ofNullable(profileDTO.getLastName()).orElse("Doe"))
                .shippingAddress(Optional.ofNullable(profileDTO.getShippingAddress()).orElse("123 Main St, Springfield, USA"))
                .staff(Optional.ofNullable(profileDTO.getStaff()).orElse("Junior"))
                .bio(Optional.ofNullable(profileDTO.getBio()).orElse("Passionate about solving complex problems through simple and elegant designs."))
                .address(Optional.ofNullable(profileDTO.getAddress()).orElse("123 Main St, Springfield, USA"))
                .phoneNumber(Optional.ofNullable(profileDTO.getPhoneNumber()).orElse("123-456-7890"))
                .notification(Optional.ofNullable(profileDTO.getNotification())
                        .map(Boolean::parseBoolean)
                        .orElse(false))
                .nameOfCard(Optional.ofNullable(profileDTO.getNameOfCard()).orElse("VISA"))
                .cardType(Optional.ofNullable(profileDTO.getCardType()).orElse("debt"))
                .cardNumber(Optional.ofNullable(profileDTO.getCardNumber()).orElse("1234-5678-9012-3456"))
                .cardExpiryDate(Optional.ofNullable(profileDTO.getCardExpiryDate()).orElse("12/25"))
                .cvv(Optional.ofNullable(profileDTO.getCvv()).orElse("123"))
                .build();
        profileRepository.save(profile);
    }

    public void updateProfile(ProfileDTO profileDTO) {

        User user = userService.getAuthenticatedUser();

        List<Profile> profiles = profileRepository.findAllByUserId(user.getUserID());
        if (profiles.isEmpty()) {
            throw new ProfileNotValueException("Profile not found for userId: " + user.getUserID());
        }
        Profile profile = profiles.get(profiles.size() - 1);
        if (profile == null) {
            throw new ProfileNotValueException("Profile not found for userId: " + user.getUserID());
        }
        profile.setBio(profileDTO.getBio());
        profile.setStaff(profileDTO.getStaff());
        profileRepository.save(profile);
    }


    public Optional<Profile> createProfile(ProfileDTO profileDTO) {
        User user = userService.getAuthenticatedUser();
        Profile profile = new Profile.Builder()
                .userId(user.getUserID())
                .firstName(Optional.ofNullable(profileDTO.getFirstName()).orElse("John"))
                .lastName(Optional.ofNullable(profileDTO.getLastName()).orElse("Doe"))
                .shippingAddress(Optional.ofNullable(profileDTO.getShippingAddress()).orElse("123 Main St, Springfield, USA"))
                .staff(Optional.ofNullable(profileDTO.getStaff()).orElse("Junior"))
                .bio(Optional.ofNullable(profileDTO.getBio()).orElse("Passionate about solving complex problems through simple and elegant designs."))
                .address(Optional.ofNullable(profileDTO.getAddress()).orElse("123 Main St, Springfield, USA"))
                .phoneNumber(Optional.ofNullable(profileDTO.getPhoneNumber()).orElse("123-456-7890"))
                .notification(Optional.ofNullable(profileDTO.getNotification())
                        .map(Boolean::parseBoolean)
                        .orElse(false))
                .nameOfCard(Optional.ofNullable(profileDTO.getNameOfCard()).orElse("VISA"))
                .cardType(Optional.ofNullable(profileDTO.getCardType()).orElse("debt"))
                .cardNumber(Optional.ofNullable(profileDTO.getCardNumber()).orElse("1234-5678-9012-3456"))
                .cardExpiryDate(Optional.ofNullable(profileDTO.getCardExpiryDate()).orElse("12/25"))
                .cvv(Optional.ofNullable(profileDTO.getCvv()).orElse("123"))
                .build();

        if (profileRepository.save(profile) != null) {
            return Optional.of(profile);
        }

        return Optional.empty();
    }

    public Optional<ProfileDTO> getProfileByUserId(Long userId) {
        List<Profile> profiles = profileRepository.findAllByUserId(userId);
        if (profiles.isEmpty()) {
            return Optional.of(new ProfileDTO());
        }
        Profile profile = profiles.get(profiles.size() - 1);
        log.info("Get profile by userId: " + userId);
        if (profile == null) {
            throw new ProfileNotValueException("Profile not found for userId: " + userId);
        }
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setFirstName(profile.getFirstName());
        profileDTO.setLastName(profile.getLastName());
        profileDTO.setShippingAddress(profile.getShippingAddress());
        profileDTO.setStaff(profile.getStaff());
        profileDTO.setBio(profile.getBio());
        profileDTO.setAddress(profile.getAddress());
        profileDTO.setPhoneNumber(profile.getPhoneNumber());
        profileDTO.setNotification(String.valueOf(profile.getNotification()));
        profileDTO.setNameOfCard(profile.getNameOfCard());
        profileDTO.setCardType(profile.getCardType());
        profileDTO.setCardNumber(profile.getCardNumber());
        profileDTO.setCardExpiryDate(profile.getCardExpiryDate());
        profileDTO.setCvv(profile.getCvv());
        log.info(profileDTO.toString() + " profileDTO");
        return Optional.of(profileDTO);
    }

    public List<ProfileDTO> getAllProfiles(Long userId) {
        List<Profile> profiles = profileRepository.findAllByUserId(userId);
        log.info("Get all profiles by userId: " + userId);
        if (profiles == null) {
            throw new ProfileNotValueException("Profiles not found for userId: " + userId);
        }
        List<ProfileDTO> profileDTOs = profiles.stream().map(profile -> {
            ProfileDTO profileDTO = new ProfileDTO();
            profileDTO.setFirstName(profile.getFirstName());
            profileDTO.setLastName(profile.getLastName());
            profileDTO.setShippingAddress(profile.getShippingAddress());
            profileDTO.setStaff(profile.getStaff());
            profileDTO.setBio(profile.getBio());
            profileDTO.setAddress(profile.getAddress());
            profileDTO.setPhoneNumber(profile.getPhoneNumber());
            profileDTO.setNotification(String.valueOf(profile.getNotification()));
            profileDTO.setNameOfCard(profile.getNameOfCard());
            profileDTO.setCardType(profile.getCardType());
            profileDTO.setCardNumber(profile.getCardNumber());
            profileDTO.setCardExpiryDate(profile.getCardExpiryDate());
            profileDTO.setCvv(profile.getCvv());
            return profileDTO;
        }).toList();
        log.info(profileDTOs.toString() + " all profiles");
        return profileDTOs;
    }

    public boolean updateNotification(String name, Boolean notification) {
        User user = userService.getUserByUsername(name);
        List<Profile> profiles = profileRepository.findAllByUserId(user.getUserID());
        if (profiles.isEmpty()) {
            return false;
        }
        Profile profile = profiles.get(profiles.size() - 1);
        if (profile == null) {
            return false;
        }
        profile.setNotification(notification);
        profileRepository.save(profile);
        emailNotificationService.sendEmail(user.getEmail(), "Notification Update", "This is the email body.");
        return true;
    }
}
