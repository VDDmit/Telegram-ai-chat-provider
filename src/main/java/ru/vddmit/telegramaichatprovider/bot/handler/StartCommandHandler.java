package ru.vddmit.telegramaichatprovider.bot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vddmit.telegramaichatprovider.entity.Message;
import ru.vddmit.telegramaichatprovider.entity.User;
import ru.vddmit.telegramaichatprovider.entity.enums.StartBotState;
import ru.vddmit.telegramaichatprovider.service.MessageService;
import ru.vddmit.telegramaichatprovider.service.UserService;
import ru.vddmit.telegramaichatprovider.utils.MessageUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@Order(1)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartCommandHandler implements ChatHandler {

    MessageUtils messageUtils;
    UserService userService;
    MessageService messageService;

    Map<Long, StartBotState> userStates = new ConcurrentHashMap<>();
    Map<Long, String> tempModelName = new ConcurrentHashMap<>();

    @Override
    public boolean canHandle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        org.telegram.telegrambots.meta.api.objects.User tgUser =
                update.getMessage().getFrom();

        if (userService.findById(tgUser.getId()) == null) {
            return false;
        } else if (update.hasMessage() && update.getMessage().hasText()) {
            return text.equals("/start")
                    || userStates.get(chatId) != null
                    && userStates.get(chatId) != StartBotState.NONE;
        }
        return false;
    }

    @Override
    public BotApiMethod<?> handle(Update update) throws TelegramApiException {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();
        org.telegram.telegrambots.meta.api.objects.User tgUser = update.getMessage().getFrom();
        User user = userService.findOrCreateUser(tgUser);


        if (text.equals("/start")) {
            userStates.put(chatId, StartBotState.WAITING_FOR_MODEL_NAME);
            tempModelName.remove(chatId);
            SendMessage sendStartMessage = messageUtils
                    .generateSendMessageWithText(update, """ 
                            Привет! Введите название нейросети (корректное название модели можете узнать в документации или через API),
                            которую хотите использовать:
                            пока поддерживаю только Gemini
                            
                            Пример: gemini-2.5-pro""");

            messageService
                    .sendAndSaveBotMessage(sendStartMessage, user);

            return null;
        }

        StartBotState state = userStates.getOrDefault(chatId, StartBotState.NONE);

        switch (state) {
            case WAITING_FOR_MODEL_NAME:
                tempModelName.put(chatId, text);
                userStates.put(chatId, StartBotState.WAITING_FOR_API_KEY);

                SendMessage sendMessage = messageUtils
                        .generateSendMessageWithText(update, "Модель сохранена. Теперь введите API-ключ:");

                messageService
                        .sendAndSaveBotMessage(sendMessage, user);
                return null;

            case WAITING_FOR_API_KEY:

                String modelName = tempModelName.get(chatId);

                user.setPrivateAiApiKey(text);
                user.setModel(modelName);
                userService.save(user);

                userStates.put(chatId, StartBotState.NONE);
                tempModelName.remove(chatId);

                int userMessageId = update.getMessage().getMessageId();

                Message message = Message.builder()
                        .id((long) userMessageId)
                        .user(user)
                        .content(text)
                        .role(false)
                        .build();
                messageService.save(message);

                messageUtils.sendEphemeralMessage(
                        update,
                        "Настройки сохранены! Можете делать запросы.",
                        chatId,
                        userMessageId,
                        30);

                return null;
            default:
                return messageUtils.generateSendMessageWithText(update,
                        "Неизвестная команда. Напишите /start для начала.");
        }
    }
}
