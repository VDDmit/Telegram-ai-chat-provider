package ru.vddmit.telegramaichatprovider.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.vddmit.telegramaichatprovider.entity.Message;
import ru.vddmit.telegramaichatprovider.entity.User;
import ru.vddmit.telegramaichatprovider.repository.MessageRepository;
import ru.vddmit.telegramaichatprovider.service.MessageService;

import java.util.Collections;
import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    MessageRepository messageRepository;

    @Override
    public void save(Message message) {
        if (message.getId() == null) {
            Long id;
            do {
                id = (long) (Math.random() * 1_000_000_000_000L);
            } while (messageRepository.existsById(id));
            message.setId(id);
        }
        messageRepository.save(message);
    }

    @Override
    public Message findById(Long id) {
        return messageRepository
                .findById(id)
                .orElseThrow(() -> new RuntimeException("Message with id: " + id + " not found."));
    }

    @Override
    public List<Message> getChatHistory(User user) {
        List<Message> history = messageRepository.findTop10ByUserOrderByCreatedAtDesc(user);
        Collections.reverse(history);
        return history;
    }

}
