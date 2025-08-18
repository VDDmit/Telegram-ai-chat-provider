package ru.vddmit.telegramaichatprovider.bot;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;
import ru.vddmit.telegramaichatprovider.controller.UpdateProcessor;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AIChatProviderBot extends SpringWebhookBot {

    String botPath;
    String botName;
    String botToken;

    UpdateProcessor updateProcessor;

    public AIChatProviderBot(SetWebhook setWebhook,
                             String botToken,
                             String botPath,
                             String botName,
                             @Lazy UpdateProcessor updateProcessor) {
        super(setWebhook, botToken);
        this.botPath = botPath;
        this.botName = botName;
        this.botToken = botToken;
        this.updateProcessor = updateProcessor;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        System.out.println("Получено обновление: " + update);
        try {
            return updateProcessor.processUpdate(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotPath() {
        return this.botPath;
    }

    @Override
    public String getBotUsername() {
        return this.botName;
    }
}
