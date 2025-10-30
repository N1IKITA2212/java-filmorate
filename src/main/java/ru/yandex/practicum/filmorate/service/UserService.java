package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(Integer id, Integer friendId) {
        User user = getUserOrThrow(id);
        User friend = getUserOrThrow(friendId);
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void removeFriend(Integer id, Integer friendId) {
        User user = getUserOrThrow(id);
        User friend = getUserOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getMutualFriends(Integer id, Integer friendId) {
        User user = getUserOrThrow(id);
        User friend = getUserOrThrow(friendId);

        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(this::getUserOrThrow)
                .toList();
    }

    public User getUserById(Integer id) {
        return getUserOrThrow(id);
    }

    public List<User> getUserFriends(Integer id) {
        User user = getUserOrThrow(id);
        return user.getFriends().stream()
                .map(this::getUserOrThrow).toList();
    }

    public List<User> getAllUsers() {
        return userStorage.getAllUsers();
    }

    public User addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        if (user.getId() == null) {
            log.error("Поле с ID пустое");
            throw new NotEnoughDataException("Не заполнено поле id", "id");
        }
        if (userStorage.isUserPresent(user.getId())) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            return userStorage.updateUser(user);
        }
        log.error("Пользователь с id {} не найден", user.getId());
        throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }

    private User getUserOrThrow(Integer id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}

