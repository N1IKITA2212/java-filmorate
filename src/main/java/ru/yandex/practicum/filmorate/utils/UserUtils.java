package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

public final class UserUtils {

    private UserUtils() {

    }

    public static User getUserOrThrow(UserStorage userStorage, Integer id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
