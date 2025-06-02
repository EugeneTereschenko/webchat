package com.example.webchat.service;

import com.example.webchat.dto.CardDTO;
import com.example.webchat.dto.ProfileDTO;
import com.example.webchat.dto.ProfileResponseDTO;
import com.example.webchat.exception.ProfileNotValueException;
import com.example.webchat.model.Card;
import com.example.webchat.model.Profile;
import com.example.webchat.model.User;
import com.example.webchat.repository.CardRepository;
import com.example.webchat.repository.ProfileRepository;
import com.example.webchat.service.impl.ActivityService;
import com.example.webchat.service.impl.CardService;
import com.example.webchat.service.impl.ProfileService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {

    private final ProfileRepository profileRepository;
    private final CardRepository cardRepository;
    private final UserService userService;
    private final EmailNotificationService emailNotificationService;
    private final ActivityService activityService;

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
                .message(Optional.ofNullable(profileDTO.getMessage()).orElse("Welcome to your profile!"))
/*                .nameOfCard(Optional.ofNullable(profileDTO.getNameOfCard()).orElse("VISA"))
                .cardType(Optional.ofNullable(profileDTO.getCardType()).orElse("debt"))
                .cardNumber(Optional.ofNullable(profileDTO.getCardNumber()).orElse("1234-5678-9012-3456"))
                .cardExpiryDate(Optional.ofNullable(profileDTO.getCardExpiryDate()).orElse("12/25"))
                .cvv(Optional.ofNullable(profileDTO.getCvv()).orElse("123"))*/
                .build();
        profileRepository.save(profile);
    }

    public Optional<ProfileResponseDTO> updateProfile(ProfileDTO profileDTO) {

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
        profile.setMessage(profileDTO.getMessage());
        Profile profileSaved = profileRepository.save(profile);

        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();

        if (profileSaved != null) {
            log.info("Profile updated for user: " + user.getUsername());
            activityService.addActivity("Profile updated", user.getUserID(), new Date());
            String token = userService.changeUsername(user.getUsername(), profileDTO.getUsername());
            profileResponseDTO.setProfileId(String.valueOf(profileSaved.getId()));
            profileResponseDTO.setMessage("Profile updated successfully");
            profileResponseDTO.setSuccess("true");
            profileResponseDTO.setToken(token);
            profileResponseDTO.setUserID(String.valueOf(user.getUserID()));
            return Optional.of(profileResponseDTO);
        } else {
            log.error("Failed to update profile for user: " + user.getUsername());
        }

        profileResponseDTO.setMessage("Failed to update profile");
        profileResponseDTO.setSuccess("false");
        return Optional.of(profileResponseDTO);
    }


    public Optional<ProfileResponseDTO> createProfile(ProfileDTO profileDTO) {
        User user = userService.getAuthenticatedUser();
        Optional<Profile> createdProfile = createProfileForUser(profileDTO, user);
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        if (createdProfile.isPresent()) {
            profileResponseDTO.setProfileId(String.valueOf(createdProfile.get().getId()));
            profileResponseDTO.setMessage("Profile created successfully");
            profileResponseDTO.setSuccess("true");
            log.info("Profile created for user: " + user.getUsername());
            return Optional.of(profileResponseDTO);
        }

        return Optional.empty();
    }

    private Optional<Profile> createProfileForUser(ProfileDTO profileDTO, User user) {
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
                .message(Optional.ofNullable(profileDTO.getMessage()).orElse("Welcome to your profile!"))
/*                .nameOfCard(Optional.ofNullable(profileDTO.getNameOfCard()).orElse("VISA"))
                .cardType(Optional.ofNullable(profileDTO.getCardType()).orElse("debt"))
                .cardNumber(Optional.ofNullable(profileDTO.getCardNumber()).orElse("1234-5678-9012-3456"))
                .cardExpiryDate(Optional.ofNullable(profileDTO.getCardExpiryDate()).orElse("12/25"))
                .cvv(Optional.ofNullable(profileDTO.getCvv()).orElse("123"))*/
                .build();
        Profile savedProfile = profileRepository.save(profile);

        if (savedProfile != null) {
            activityService.addActivity("Profile create", user.getUserID(), new Date());
            return Optional.of(savedProfile);
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
        profileDTO.setMessage(profile.getMessage());

        log.info(profileDTO.toString() + " profileDTO");
        return Optional.of(profileDTO);
    }

    public Optional<Profile> getProfileByUserName(String username) {
        User user = userService.getUserByUsername(username);
        log.info("Get profile by userId: " + user.getUserID());
        return profileRepository.findAllByUserId(user.getUserID())
                .stream()
                .findFirst()
                .or(() -> {
                    log.warn("Profile not found for userId: " + user.getUserID());
                    return Optional.empty();
                });
    }

    public Optional<Profile> getProfileByUserId() {
        User user = userService.getAuthenticatedUser();
        log.info("Get profile by userId: " + user.getUserID());
        return profileRepository.findAllByUserId(user.getUserID())
                .stream()
                .findFirst()
                .or(() -> {
                    log.warn("Profile not found for userId: " + user.getUserID());
                    return Optional.empty();
                });
    }

    public List<ProfileDTO> getAllProfiles() {
        User user = userService.getAuthenticatedUser();
        List<Profile> profiles = profileRepository.findAllByUserId(user.getUserID());
        log.debug("Get all profiles by userId: " + user.getUserID());
        if (profiles == null) {
            throw new ProfileNotValueException("Profiles not found for userId: " + user.getUserID());
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
            profileDTO.setMessage(profile.getMessage());

            return profileDTO;
        }).toList();
        log.debug(profileDTOs.toString() + " all profiles");
        return profileDTOs;
    }

    public boolean addCardToProfile(Long cardId) {
        User user = userService.getAuthenticatedUser();
        List<Profile> profiles = profileRepository.findAllByUserId(user.getUserID());
        if (profiles.isEmpty()) {
            return false;
        }
        Profile profile = profiles.get(profiles.size() - 1);
        if (profile == null) {
            return false;
        }
        Optional<Card> card = Optional.ofNullable(cardRepository.getCardById(cardId).orElse(null));
        if (card.isEmpty()) {
            return false;
        }
        profile.setCard(card.get());
        profileRepository.save(profile);
        return true;
    }

    public Optional<CardDTO> createAndAddCardToProfile(CardDTO cardDTO) {
        if (cardDTO == null || cardDTO.getCardExpiryDate() == null || cardDTO.getCardNumber() == null) {
            return Optional.empty(); // Return empty if the DTO is invalid
        }
        // Create a new Card object from the DTO
        Card card = new Card.Builder()
                .cardNumber(cardDTO.getCardNumber())
                .cardExpiryDate(cardDTO.getCardExpiryDate())
                .cardType(cardDTO.getCardType())
                .nameOfCard(cardDTO.getNameOfCard())
                .build();
        // Save the card to the repository
        Card savedCard = cardRepository.save(card);
        if (savedCard == null) {
            return Optional.empty();
        }
        if (!addCardToProfile(card.getId())) {
           return Optional.empty();
        }
        CardDTO savedCardDTO = new CardDTO.Builder()
                .id(savedCard.getId())
                .cardType(savedCard.getCardType())
                .nameOfCard(savedCard.getNameOfCard())
                .cardNumber(savedCard.getCardNumber())
                .cardExpiryDate(savedCard.getCardExpiryDate())
                .cvv(savedCard.getCvv())
                .build();
        return Optional.of(savedCardDTO);
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

    public Optional<ProfileDTO> getProfile() {
        User user = userService.getAuthenticatedUser();
        Optional<Profile> profile = getProfileByUserId();

        if (profile.isPresent()) {
            ProfileDTO profileDTO = new ProfileDTO.Builder()
                    .firstName(profile.get().getFirstName())
                    .lastName(profile.get().getLastName())
                    .shippingAddress(profile.get().getShippingAddress())
                    .staff(profile.get().getStaff())
                    .bio(profile.get().getBio())
                    .address(profile.get().getAddress())
                    .phoneNumber(profile.get().getPhoneNumber())
                    .notification(String.valueOf(profile.get().getNotification()))
                    .message(profile.get().getMessage())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .isActive(String.valueOf(user.isActive()))
                    .notification(profile.get().getNotification() ? "true" : "false")
                    .twoFactors(String.valueOf(user.isTwoFactorEnabled()))
                    .build();
            return Optional.of(profileDTO);
        }

        ProfileDTO profileDTO = new ProfileDTO.Builder()
/*                .firstName(profile.get().getFirstName())
                .lastName(profile.get().getLastName())
                .shippingAddress(profile.get().getShippingAddress())
                .staff(profile.get().getStaff())
                .bio(profile.get().getBio())
                .address(profile.get().getAddress())
                .phoneNumber(profile.get().getPhoneNumber())
                .notification(String.valueOf(profile.get().getNotification()))
                .message(profile.get().getMessage())*/
                .username(user.getUsername())
                .email(user.getEmail())
                .isActive(String.valueOf(user.isActive()))
                .notification(String.valueOf(false))
                .twoFactors(String.valueOf(user.isTwoFactorEnabled()))
                .build();
        return Optional.of(profileDTO);
    }

    public Optional<CardDTO> getCard() {
        Optional<Profile> profile = getProfileByUserId();
        log.info("profile is " + profile.toString());
        if (profile.isPresent()) {
            Card card = profile.get().getCard();
            if (card != null) {

                CardDTO cardDTO = new CardDTO.Builder()
                        .id(card.getId())
                        .cardType(card.getCardType())
                        .nameOfCard(card.getNameOfCard())
                        .cardNumber(card.getCardNumber())
                        .cardExpiryDate(card.getCardExpiryDate())
                        .cvv(card.getCvv())
                        .build();
                return Optional.of(cardDTO);
            }
        }
        return Optional.empty();
    }

    public Optional<ProfileResponseDTO> getUpdateNotification(String notification) {
        log.info("Update user notification " + notification);
        User user = userService.getAuthenticatedUser();
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        if (notification.equals("true")) {
            Boolean result = updateNotification(user.getUsername(), true);
            activityService.addActivity("Update add Email notifications", user.getUserID(), new Date());
            profileResponseDTO.setMessage("Notification updated successfully");
            profileResponseDTO.setSuccess(String.valueOf(result));

        } else {
            Boolean result = updateNotification(user.getUsername(), false);
            profileResponseDTO.setMessage("Notification updated successfully");
            profileResponseDTO.setSuccess(String.valueOf(result));
        }
        return Optional.of(profileResponseDTO);
    }

    public Optional<ProfileResponseDTO> getUpdateMessage(String message) {
        User user = userService.getAuthenticatedUser();
        List<Profile> profiles = profileRepository.findAllByUserId(user.getUserID());
        if (profiles.isEmpty()) {
            return Optional.empty();
        }
        Profile profile = profiles.get(profiles.size() - 1);
        if (profile == null) {
            return Optional.empty();
        }
        profile.setMessage(message);
        profileRepository.save(profile);
        ProfileResponseDTO profileResponseDTO = new ProfileResponseDTO();
        profileResponseDTO.setMessage("Message updated successfully");
        profileResponseDTO.setSuccess("true");
        log.info("Update user message " + message);
        activityService.addActivity("Update message", user.getUserID(), new Date());

        return Optional.of(profileResponseDTO);
    }

    public HashMap<String, String> getActivityByUser(String numOfLogs) {
        log.debug("Get user activity " + numOfLogs);
        User user = userService.getAuthenticatedUser();
        HashMap<String, String> response = activityService.getActivitiesByUserId(user.getUserID(), Integer.valueOf(numOfLogs));
        return response;
    }
}
