package com.example.webchat.repository;

import com.example.webchat.model.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, Long> {
    List<Profile> findAllByUserId(Long userId);

    Optional<Object> findByUserId(Long userID);
}
