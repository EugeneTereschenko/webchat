package com.example.webchat.service;
import com.example.webchat.model.Image;
import com.example.webchat.repository.ImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    public Long saveImage(MultipartFile file, Long userId) throws IOException {
        Image image = new Image();
        image.setName(file.getOriginalFilename());
        image.setData(file.getBytes());
        image.setUserId(userId);
        return imageRepository.save(image).getId();
    }

    public Image getImage(Long id) {
        return imageRepository.findById(id).orElse(null);
    }

    public Image getImageByUserId(Long userId) {
        Optional<Image> image = imageRepository.findByUserId(userId);
        return image.orElse(null);
    }
}
