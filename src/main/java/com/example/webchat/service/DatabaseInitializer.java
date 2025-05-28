package com.example.webchat.service;

import com.example.webchat.model.User;
import com.example.webchat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DatabaseInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner initDatabase(UserRepository userRepository) {
        return args -> {
            User user = new User();
            user.setUsername("John Doe");
            user.setEmail("john.doe@example.com");
            user.setPassword(passwordEncoder.encode("password123"));
            user.setSalt("password123");
            userRepository.save(user);
            System.out.println("User saved to database: " + user.getUsername());



            // You can add more initial data here if needed
            User anotherUser = new User();
            anotherUser.setUsername("wer");
            anotherUser.setEmail("wer@test.com");
            anotherUser.setPassword(passwordEncoder.encode("TESTWORLD"));
            anotherUser.setSalt("TESTWORLD");
            userRepository.save(anotherUser);
            System.out.println("Another user saved to database: " + anotherUser.getUsername());

            // You can add more initial data here if needed
            User anotherThirdUser = new User();
            anotherThirdUser.setUsername("war");
            anotherThirdUser.setEmail("war@test.com");
            anotherThirdUser.setPassword(passwordEncoder.encode("TESTWORLD"));
            anotherThirdUser.setSalt("TESTWORLD");
            userRepository.save(anotherThirdUser);
            System.out.println("Another user saved to database: " + anotherThirdUser.getUsername());
        };
    }
}
