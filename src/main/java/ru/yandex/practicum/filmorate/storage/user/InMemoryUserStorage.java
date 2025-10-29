package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    @Override
    public Optional<User> getUser(int userId) {
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(User user) {
        user.setId(getNewId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id {} добавлен ", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getId() == null) {
            log.error("Поле с ID пустое");
            throw new NotEnoughDataException("Не заполнено поле id", "id");
        }
        if (users.containsKey(user.getId())) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(user.getId(), user);
            log.info("Пользователь с id {} обновлен", user.getId());
            return user;
        }
        log.error("Пользователь с id {} не найден", user.getId());
        throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }

    private Integer getNewId() {
        Integer currentId = users.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++currentId;
    }
}
