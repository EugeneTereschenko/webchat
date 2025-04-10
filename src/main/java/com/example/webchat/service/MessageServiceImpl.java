package com.example.webchat.service;

import com.example.webchat.model.Chat;
import com.example.webchat.model.Message;
import com.example.webchat.repository.MessageRepository;
import com.example.webchat.service.impl.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;

    @Override
    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    @Override
    public Optional<Message> saveMessage(Chat chat, Message message) {
        Message messageToSave = new Message();
        messageToSave.setUser(Optional.ofNullable(message.getUser()).orElse(""));
        messageToSave.setMessage(Optional.ofNullable(message.getMessage()).orElse(""));
        messageToSave.setChat(chat);

        messageRepository.save(messageToSave);
        return Optional.of(messageToSave);
    }


    @Override
    public List<Message> getMessagesByChatId(Long chatId) {
        return messageRepository.findByChatId(chatId);
    }
}
