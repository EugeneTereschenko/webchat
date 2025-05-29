package com.example.webchat.service;

import com.example.webchat.model.Chat;
import com.example.webchat.model.Image;
import com.example.webchat.model.Message;
import com.example.webchat.model.User;
import com.example.webchat.repository.ChatRepository;
import com.example.webchat.repository.ImageRepository;
import com.example.webchat.repository.MessageRepository;
import com.example.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DatabaseInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository,
                                          ChatRepository chatRepository, MessageRepository messageRepository, ImageRepository imageRepository) {
        return args -> {
            User user = new User();
            user.setUsername("John Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setSalt("password123");
            user = userRepository.save(user);
            System.out.println("User saved to database: " + user.getUsername());

            Image image = new Image.Builder()
                    .name("Avatar")
                    .userId(user.getUserID())
                    .data(getDefaultAvatarImage(8))
                    .build();

            imageRepository.save(image);

            // You can add more initial data here if needed
            User anotherUser = new User();
            anotherUser.setUsername("wer");
            anotherUser.setEmail("wer@test.com");
            anotherUser.setPassword(passwordEncoder.encode("TESTWORLD"));
            anotherUser.setSalt("TESTWORLD");
            userRepository.save(anotherUser);
            System.out.println("Another user saved to database: " + anotherUser.getUsername());

            Image image2 = new Image.Builder()
                    .name("Avatar")
                    .userId(anotherUser.getUserID())
                    .data(getDefaultAvatarImage(5))
                    .build();

            imageRepository.save(image2);

            // You can add more initial data here if needed
            User anotherThirdUser = new User();
            anotherThirdUser.setUsername("war");
            anotherThirdUser.setEmail("war@test.com");
            anotherThirdUser.setPassword(passwordEncoder.encode("TESTWORLD"));
            anotherThirdUser.setSalt("TESTWORLD");
            userRepository.save(anotherThirdUser);
            System.out.println("Another user saved to database: " + anotherThirdUser.getUsername());

            Image image3 = new Image.Builder()
                    .name("Avatar")
                    .userId(anotherThirdUser.getUserID())
                    .data(getDefaultAvatarImage(6))
                    .build();

            imageRepository.save(image3);

            // You can add more initial data here if needed
            User anotherFourUser = new User();
            anotherFourUser.setUsername("Danny Smith");
            anotherFourUser.setEmail("danny.smith@example.com");
            anotherFourUser.setPassword(passwordEncoder.encode("TESTWORLD"));
            anotherFourUser.setSalt("TESTWORLD");
            userRepository.save(anotherFourUser);
            System.out.println("Another user saved to database: " + anotherFourUser.getUsername());


            Image image4 = new Image.Builder()
                    .name("Avatar")
                    .userId(anotherFourUser.getUserID())
                    .data(getDefaultAvatarImage(7))
                    .build();

            imageRepository.save(image4);

            // You can add more initial data here if needed
            User another5User = new User();
            another5User.setUsername("Alex Steward");
            another5User.setEmail("alex.steward@example.com");
            another5User.setPassword(passwordEncoder.encode("TESTWORLD"));
            another5User.setSalt("TESTWORLD");
            userRepository.save(another5User);
            System.out.println("Another user saved to database: " + another5User.getUsername());

            Image image5 = new Image.Builder()
                    .name("Avatar")
                    .userId(another5User.getUserID())
                    .data(getDefaultAvatarImage(2))
                    .build();

            imageRepository.save(image5);


            Chat chat = new Chat.Builder()
                    .chatName("COOL")
                    .users(List.of("John Doe", "Danny Smith", "Alex Steward", "wer", "war"))
                    .build();

            chatRepository.save(chat);

            Message message = new Message.Builder()
                    .user("John Doe")
                    .message("Hello, this is a test message!")
                    .chat(chat)
                    .isRead(false)
                    .usersRead(List.of())
                    .usersToken(List.of())
                    .time(new java.util.Date())
                    .build();

            messageRepository.save(message);


            Message message2 = new Message.Builder()
                    .user("wer")
                    .message("Hello, this is a test message from wer!")
                    .chat(chat)
                    .isRead(false)
                    .usersRead(List.of())
                    .usersToken(List.of())
                    .time(new java.util.Date())
                    .build();

            messageRepository.save(message2);

            Message message3 = new Message.Builder()
                    .user("war")
                    .message("Hello, this is a test message from war!")
                    .chat(chat)
                    .isRead(false)
                    .usersRead(List.of())
                    .usersToken(List.of())
                    .time(new java.util.Date())
                    .build();

            messageRepository.save(message3);

            Message message4 = new Message.Builder()
                    .user("Danny Smith")
                    .message("Hello, this is a test message from Danny Smith!")
                    .chat(chat)
                    .isRead(false)
                    .usersRead(List.of())
                    .usersToken(List.of())
                    .time(new java.util.Date())
                    .build();

            messageRepository.save(message4);




        };
    }

    public static byte[] getDefaultAvatarImage(int num) {
        try (java.io.InputStream in = new java.net.URL("https://mdbcdn.b-cdn.net/img/Photos/Avatars/avatar-" + num + ".webp").openStream();
             java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream()) {
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }
}
