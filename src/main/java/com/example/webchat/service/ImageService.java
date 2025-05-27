package com.example.webchat.service;
import com.example.webchat.exception.ImageNotFoundException;
import com.example.webchat.model.Image;
import com.example.webchat.model.User;
import com.example.webchat.repository.ImageRepository;
import com.example.webchat.service.impl.ActivityService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Slf4j
@AllArgsConstructor
@Service
public class ImageService {


    private final ImageRepository imageRepository;
    private final UserService userService;
    private final ActivityService activityService;

    public Long saveImage(MultipartFile file, Long userId) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setData(file.getBytes());
        image.setUserId(userId);
        return imageRepository.save(image).getId();
    }

    public Image getImage(Long id) {
        return imageRepository.findById(id).orElseThrow(() -> new ImageNotFoundException("Image not found"));
    }

    public Optional<Image> getImageByUserId(Long userId) {
        Optional<Image> image = imageRepository.findByUserId(userId);
        if (image.isEmpty()) {
            return Optional.of(new Image());
        }
        return image;
    }

    public Optional<Image> getImageByUserName(String userName) {
        Long userId = userService.getUserIdByUserName(userName);
        Optional<Image> image = imageRepository.findByUserId(userId);
        if (image.isEmpty()) {
            return Optional.of(new Image());
        }
        return image;
    }

    public String uploadImageToDatabase(MultipartFile file) {
        try {
            User user = userService.getAuthenticatedUser();
            log.info("User upload a photo " + user.getUsername());
            Long imageId = saveImage(file, user.getUserID());
            activityService.addActivity("Upload image", user.getUserID(), new Date());
            return "Image uploaded successfully with ID: " + imageId;
        } catch (Exception e) {
            log.error("Error uploading image: ", e.getMessage());
            return "Error uploading image: ";
        }
    }

    public byte[] getImageForUser() {
        try {
            User user = userService.getAuthenticatedUser();
            byte[] imageData = getImageByUserId(user.getUserID()).get().getData();
            return imageData;
        } catch (Exception e) {
            log.error("Error retrieving image for user: ", e.getMessage());
            return null;
        }
    }
}
