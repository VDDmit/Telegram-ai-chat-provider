package ru.vddmit.telegramaichatprovider.service;

import ru.vddmit.telegramaichatprovider.entity.User;

public interface UserService {

    void save(User user);

    User findById(Long id);

    User findOrCreateUser(org.telegram.telegrambots.meta.api.objects.User tgUser);
}
