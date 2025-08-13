package ru.vddmit.telegramaichatprovider.service;

import ru.vddmit.telegramaichatprovider.entity.Message;
import ru.vddmit.telegramaichatprovider.entity.User;

import java.util.List;

public interface MessageService {

    void save(Message message);

    Message findById(Long id);

    List<Message> getChatHistory(User user);
}
