package ru.vddmit.telegramaichatprovider.utils;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vddmit.telegramaichatprovider.bot.AIChatProviderBot;
import ru.vddmit.telegramaichatprovider.service.MessageLifecycleService;

@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class MessageUtils {

    AIChatProviderBot bot;
    final MessageLifecycleService messageLifecycleService;

    @Autowired
    public void setBot(@Lazy AIChatProviderBot bot) {
        this.bot = bot;
    }

    public void sendEphemeralMessage(Update update, String text, long chatId, int userMessageId, int delayInSeconds) throws TelegramApiException {
        SendMessage sendMessage = generateSendMessageWithText(update, text);
        Message sentMessage = bot.execute(sendMessage);
        int botMessageId = sentMessage.getMessageId();
        messageLifecycleService.scheduleMessagesDeletion(chatId, userMessageId, botMessageId, delayInSeconds);
    }


    public SendMessage generateSendMessageWithText(Update update, String text) {
        var message = update.getMessage();
        var sendMessage = new SendMessage();
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setText(text);
        return sendMessage;
    }
}
