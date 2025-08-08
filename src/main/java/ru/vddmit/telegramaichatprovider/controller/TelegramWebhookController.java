package ru.vddmit.telegramaichatprovider.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.vddmit.telegramaichatprovider.bot.AIChatProviderBot;

@RestController
public class TelegramWebhookController {

    private final AIChatProviderBot bot;

    public TelegramWebhookController(AIChatProviderBot bot) {
        this.bot = bot;
    }

    @PostMapping("/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return bot.onWebhookUpdateReceived(update);
    }
}