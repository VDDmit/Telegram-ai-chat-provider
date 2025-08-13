package ru.vddmit.telegramaichatprovider.bot.handler;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface ChatHandler {

    boolean canHandle(Update update);

    BotApiMethod<?> handle(Update update);
}
