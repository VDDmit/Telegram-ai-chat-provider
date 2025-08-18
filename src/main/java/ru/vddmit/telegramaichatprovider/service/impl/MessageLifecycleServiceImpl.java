package ru.vddmit.telegramaichatprovider.service.impl;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vddmit.telegramaichatprovider.bot.AIChatProviderBot;
import ru.vddmit.telegramaichatprovider.service.MessageLifecycleService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
public class MessageLifecycleServiceImpl implements MessageLifecycleService {

    AIChatProviderBot bot;
    final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public void setBot(@Lazy AIChatProviderBot bot) {
        this.bot = bot;
    }

    @Override
    public void scheduleMessagesDeletion(long chatId, int userMessageId, int botMessageId, long delayInSeconds) {
        Runnable deletionTask = () -> {
            try {
                bot.execute(new DeleteMessage(String.valueOf(chatId), userMessageId));
                bot.execute(new DeleteMessage(String.valueOf(chatId), botMessageId));
                log.info("Successfully deleted messages {} and {} in chat {}", userMessageId, botMessageId, chatId);
            } catch (TelegramApiException e) {
                log.warn("Could not delete messages in chat {}: {}", chatId, e.getMessage());
            } catch (NullPointerException e) {
                log.error("AIChatProviderBot is null in MessageLifecycleService. The circular dependency might not be fully resolved.", e);
            }
        };
        scheduler.schedule(deletionTask, delayInSeconds, TimeUnit.SECONDS);
    }
}