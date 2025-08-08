package ru.vddmit.telegramaichatprovider.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import ru.vddmit.telegramaichatprovider.bot.AIChatProviderBot;
import ru.vddmit.telegramaichatprovider.controller.UpdateProcessor;

@Configuration
public class AppConfig {

    @Value("${telegram.webhook-path}")
    String webhookPath;
    @Value("${telegram.bot-name}")
    String botName;
    @Value("${telegram.bot-token}")
    String botToken;

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder().url(webhookPath).build();
    }

    @Bean
    public AIChatProviderBot aiChatProviderBot(SetWebhook setWebhook, UpdateProcessor updateProcessor) {
        return new AIChatProviderBot(setWebhook, botToken, webhookPath, botName, updateProcessor);
    }

}
