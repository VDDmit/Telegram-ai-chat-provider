package ru.vddmit.telegramaichatprovider.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vddmit.telegramaichatprovider.entity.User;
import ru.vddmit.telegramaichatprovider.repository.UserRepository;
import ru.vddmit.telegramaichatprovider.service.UserService;
import ru.vddmit.telegramaichatprovider.utils.JsonUtils;

import java.util.Optional;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    UserRepository userRepository;

    @Override
    @Transactional
    public void save(User user) {
        userRepository.save(user);
        System.out.println("User saved: " + JsonUtils.toJson(user));
    }

    @Override
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
    }

    @Override
    @Transactional
    public User findOrCreateUser(org.telegram.telegrambots.meta.api.objects.User tgUser) {
        Optional<User> user = userRepository.findById(tgUser.getId());
        return user.orElseGet(() -> {
            User newUser = new User();
            newUser.setId(tgUser.getId());
            newUser.setUsername(tgUser.getUserName());
            newUser.setFirstName(tgUser.getFirstName());
            newUser.setLastName(tgUser.getLastName());
            newUser.setLanguageCode(tgUser.getLanguageCode());

            return userRepository.save(newUser);
        });
    }
}
