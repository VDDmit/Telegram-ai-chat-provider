package ru.vddmit.telegramaichatprovider.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vddmit.telegramaichatprovider.entity.Message;
import ru.vddmit.telegramaichatprovider.entity.User;

import java.util.List;

public interface MessageService {

    void sendAndSaveBotMessage(SendMessage sendMessage, User user) throws TelegramApiException;

    void save(Message message);

    Message findById(Long id);

    List<Message> getChatHistory(User user);
}
