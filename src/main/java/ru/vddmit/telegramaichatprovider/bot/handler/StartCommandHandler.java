package ru.vddmit.telegramaichatprovider.bot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vddmit.telegramaichatprovider.entity.User;
import ru.vddmit.telegramaichatprovider.entity.enums.StartBotState;
import ru.vddmit.telegramaichatprovider.service.UserService;
import ru.vddmit.telegramaichatprovider.utils.MessageUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class StartCommandHandler {

    MessageUtils messageUtils;
    UserService userService;

    Map<Long, StartBotState> userStates = new ConcurrentHashMap<>();
    Map<Long, String> tempModelName = new ConcurrentHashMap<>();

    public boolean canHandle(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Long chatId = update.getMessage().getChatId();
            String text = update.getMessage().getText();
            return text.equals("/start") || userStates.get(chatId) != null && userStates.get(chatId) != StartBotState.NONE;
        }
        return false;
    }

    public BotApiMethod<?> handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (text.equals("/start")) {
            userStates.put(chatId, StartBotState.WAITING_FOR_MODEL_NAME);
            tempModelName.remove(chatId);
            return messageUtils
                    .generateSendMessageWithText(update, """ 
                            Привет! Введите название нейросети (корректное название модели можете узнать в документации или через API),
                            которую хотите использовать:
                            ||Пример:gemini-2.5-pro||""");
        }

        StartBotState state = userStates.getOrDefault(chatId, StartBotState.NONE);

        switch (state) {
            case WAITING_FOR_MODEL_NAME:
                tempModelName.put(chatId, text);
                userStates.put(chatId, StartBotState.WAITING_FOR_API_KEY);
                return messageUtils
                        .generateSendMessageWithText(update, "Модель сохранена. Теперь введите API-ключ:");
            case WAITING_FOR_API_KEY:
                String modelName = tempModelName.get(chatId);
                String apiKey = text;
                org.telegram.telegrambots.meta.api.objects.User tgUser = update.getMessage().getFrom();
                User user = new User();
                user.setId(tgUser.getId());
                user.setUsername(tgUser.getUserName());
                user.setFirstName(tgUser.getFirstName());
                user.setLastName(tgUser.getLastName());
                user.setLanguageCode(tgUser.getLanguageCode());
                user.setPrivateAiApiKey(apiKey);
                user.setModel(modelName);
                userService.save(user);
                userStates.put(chatId, StartBotState.NONE);
                tempModelName.remove(chatId);
                return messageUtils.generateSendMessageWithText(update,
                        "Настройки сохранены! Можете делать запросы.");
            default:
                return messageUtils.generateSendMessageWithText(update,
                        "Неизвестная команда. Напишите /start для начала.");
        }
    }
}
