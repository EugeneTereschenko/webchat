package com.example.webchat.repository;

import com.example.webchat.model.ChatUsers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

@Repository
public interface ChatUsersRepository extends JpaRepository<ChatUsers, Long> {
    @Override
    Optional<ChatUsers> findById(Long aLong);

    //Optional<ChatUsers> findByUserIdAndChatName(Long userId, String chatName);

    Optional<ChatUsers> findByUserIdAndChatId(Long userId, Long chatId);

    @Query("SELECT c.time FROM ChatUsers c WHERE c.userId = :userId AND c.chatId = :chatId")
    Date findTimeByUserIdAndChatId(@Param("userId") Long userId, @Param("chatId") Long chatId);
}
