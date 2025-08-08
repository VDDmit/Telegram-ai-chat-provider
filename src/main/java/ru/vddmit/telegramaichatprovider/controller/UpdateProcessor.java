package ru.vddmit.telegramaichatprovider.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vddmit.telegramaichatprovider.bot.handler.StartCommandHandler;
import ru.vddmit.telegramaichatprovider.utils.MessageUtils;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Component
@Slf4j
public class UpdateProcessor {

    MessageUtils messageUtils;
    StartCommandHandler startCommandHandler;

    public BotApiMethod<?> processUpdate(Update update) {
        if (update == null) {
            System.out.println("Update is null");
            return null;
        }

        if (update.hasMessage()) {
            return distributeMessagesByType(update);
        } else {
            System.out.println("Unsupported update type {}" + update);
            return createUnsupportedMessageTypeView(update);
        }
    }

    private BotApiMethod<?> distributeMessagesByType(Update update) {
        var message = update.getMessage();
        if (message.hasText()) {
            return processTextMessage(update);
        } else {
            return createUnsupportedMessageTypeView(update);
        }

    }

    private BotApiMethod<?> processTextMessage(Update update) {
        if (startCommandHandler.canHandle(update)) {
            return startCommandHandler.handle(update);
        }
        // TODO: сделать if для ИИ сообщений


        // TODO: добавить реальный обработчик, а не заглушку
        System.out.println("Processing text message: {}" + update.getMessage().getText());
        return messageUtils.generateSendMessageWithText(update, "Я не знаю, что с этим делать. Пожалуйста, начните с команды /start, чтобы настроить меня.");
    }

    private SendMessage createUnsupportedMessageTypeView(Update update) {
        return messageUtils.generateSendMessageWithText(update, "Неподдерживаемый тип сообщения!");
    }
}
