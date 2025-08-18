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
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            return text.equals("/start") || userStates.containsKey(chatId);
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
            return handleStartCommand(update, user);
        }

        return handleSetupSteps(update, user);
    }

    /**
     * Обрабатывает команду /start.
     * Проверяет, настроен ли пользователь. Если да - приветствует. Если нет - начинает настройку.
     */
    private BotApiMethod<?> handleStartCommand(Update update, User user) throws TelegramApiException {
        if (user.getPrivateAiApiKey() != null && !user.getPrivateAiApiKey().isBlank()) {
            String welcomeBackText = String.format(
                    "С возвращением, %s! 👋\nВаша текущая модель: `%s`.\nПросто отправьте мне свой вопрос.",
                    user.getFirstName(),
                    user.getModel()
            );
            SendMessage welcomeMessage = messageUtils.generateSendMessageWithText(update, welcomeBackText);
            welcomeMessage.enableMarkdown(true);
            messageService.sendAndSaveBotMessage(welcomeMessage, user);
        } else {
            userStates.put(user.getId(), StartBotState.WAITING_FOR_MODEL_NAME);
            tempModelName.remove(user.getId());

            String startText = """
                    Привет! Давайте настроим вашего AI-помощника.
                    
                    Сначала введите название нейросети, которую хотите использовать.
                    Корректное название модели можно узнать в документации или через API.
                    
                    *Пока поддерживается только Gemini.*
                    
                    Пример: `gemini-1.5-pro`""";

            SendMessage sendStartMessage = messageUtils.generateSendMessageWithText(update, startText);
            sendStartMessage.enableMarkdown(true); // Включаем Markdown для форматирования

            messageService.sendAndSaveBotMessage(sendStartMessage, user);
        }
        return null;
    }

    /**
     * Обрабатывает шаги настройки после команды /start (ввод модели, ввод ключа).
     */
    private BotApiMethod<?> handleSetupSteps(Update update, User user) throws TelegramApiException {
        Long chatId = user.getId();
        String text = update.getMessage().getText();
        StartBotState state = userStates.getOrDefault(chatId, StartBotState.NONE);

        switch (state) {
            case WAITING_FOR_MODEL_NAME:
                tempModelName.put(chatId, text);
                userStates.put(chatId, StartBotState.WAITING_FOR_API_KEY);

                SendMessage apiKeyMessage = messageUtils
                        .generateSendMessageWithText(update, "Отлично! Теперь введите ваш API-ключ:");
                messageService.sendAndSaveBotMessage(apiKeyMessage, user);
                break;
            case WAITING_FOR_API_KEY:
                String modelName = tempModelName.get(chatId);
                user.setPrivateAiApiKey(text);
                user.setModel(modelName);
                userService.save(user);

                userStates.remove(chatId);
                tempModelName.remove(chatId);

                int userMessageId = update.getMessage().getMessageId();
                messageUtils.sendEphemeralMessage(
                        update,
                        "Настройки сохранены! Можете делать запросы.",
                        chatId,
                        userMessageId,
                        30);
                break;

            default:

                messageService.sendAndSaveBotMessage(
                        messageUtils.generateSendMessageWithText(update, "Неизвестная команда. Напишите /start для начала."),
                        user
                );
                break;
        }
        return null;
    }
}