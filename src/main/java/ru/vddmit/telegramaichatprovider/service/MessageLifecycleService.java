package ru.vddmit.telegramaichatprovider.service;

public interface MessageLifecycleService {

    void scheduleMessagesDeletion(long chatId, int userMessageId, int botMessageId, long delayInSeconds);
}
