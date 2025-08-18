package ru.vddmit.telegramaichatprovider.bot.handler;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.vddmit.telegramaichatprovider.entity.Message;
import ru.vddmit.telegramaichatprovider.entity.User;
import ru.vddmit.telegramaichatprovider.service.AiService;
import ru.vddmit.telegramaichatprovider.service.MessageService;
import ru.vddmit.telegramaichatprovider.service.UserService;
import ru.vddmit.telegramaichatprovider.service.factory.AiServiceFactory;
import ru.vddmit.telegramaichatprovider.utils.MessageUtils;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Order(2)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatWithAiHandler implements ChatHandler {

    MessageUtils messageUtils;
    UserService userService;
    MessageService messageService;
    AiServiceFactory aiServiceFactory;

    @Override
    public boolean canHandle(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public BotApiMethod<?> handle(Update update) throws TelegramApiException {

        long chatId = update.getMessage().getChatId();
        int userMessageId = update.getMessage().getMessageId();
        String text = update.getMessage().getText();
        org.telegram.telegrambots.meta.api.objects.User tgUser = update.getMessage().getFrom();
        User user = userService.findOrCreateUser(tgUser);

        if (user.getModel() == null || user.getPrivateAiApiKey() == null) {
            messageUtils.sendEphemeralMessage(
                    update,
                    "Кажется, вы еще не настроили модель и API-ключ. Пожалуйста, используйте команду /start для настройки.",
                    chatId,
                    userMessageId,
                    30
            );
            return null;
        }

        Message userMessage = new Message();
        userMessage.setUser(user);
        userMessage.setContent(text);
        userMessage.setRole(false);
        messageService.save(userMessage);

        try {
            AiService aiService = aiServiceFactory.getAiServices(user.getModel());

            String textWitHistory = buildPromptWithHistory(user, text);
            String response = aiService.generateResponse(textWitHistory, user.getPrivateAiApiKey(), user.getModel());

            Message aiMessage = new Message();
            aiMessage.setUser(user);
            aiMessage.setContent(response);
            aiMessage.setRole(true);
            messageService.save(aiMessage);

            return messageUtils.generateSendMessageWithText(update, response);

        } catch (IllegalArgumentException e) {
            return messageUtils
                    .generateSendMessageWithText(update,
                            "Неподдерживаемая модель ИИ. Проверьте настройки через /start. Ошибка: "
                                    + e.getMessage());

        } catch (Exception e) {
            return messageUtils.generateSendMessageWithText(update,
                    "Произошла ошибка при обращении к нейросети. " +
                            "Пожалуйста, проверьте правильность API-ключа и названия модели в /start. " +
                            "Детали ошибки: " + e.getMessage());
        }
    }

    private String buildPromptWithHistory(User user, String text) {
        List<Message> history = messageService.getChatHistory(user);

        if (history.isEmpty()) {
            return text;
        }

        String historyString = history.stream()
                .map(message -> message.isRole() ? "AI: " + message.getContent() : "Human: " + message.getContent())
                .collect(Collectors.joining("\n"));

        return "Current prompt: " + text +
                "\n\nPrevious conversation history:\n" +
                historyString;
    }

}
