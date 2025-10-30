package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
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
        users.put(user.getId(), user);
        log.info("Пользователь с id {} добавлен ", user.getId());
        return user;
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь с id {} обновлен", user.getId());
        return user;
    }

    private Integer getNewId() {
        Integer currentId = users.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++currentId;
    }

    @Override
    public boolean isUserPresent(Integer id) {
        return users.containsKey(id);
    }
}
