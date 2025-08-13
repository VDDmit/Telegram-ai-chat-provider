package ru.vddmit.telegramaichatprovider.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vddmit.telegramaichatprovider.bot.handler.ChatHandler;
import ru.vddmit.telegramaichatprovider.utils.MessageUtils;

import java.util.List;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Slf4j
public class UpdateProcessor {

    List<ChatHandler> handlers;
    MessageUtils messageUtils;

    public BotApiMethod<?> processUpdate(Update update) {

        if (update == null) {
            log.warn("Получено пустое обновление (update is null).");
            return null;
        }
        for (ChatHandler handler : handlers) {
            if (handler.canHandle(update)) {
                return handler.handle(update);
            }
        }
        return createUnsupportedMessageTypeView(update);
    }

    private SendMessage createUnsupportedMessageTypeView(Update update) {
        return messageUtils.generateSendMessageWithText(update,
                "Я не знаю, что с этим делать. Пожалуйста, начните с команды /start, чтобы настроить меня.");
    }
}
