package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.UserUtils;

import java.util.List;

@Service
@AllArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public void addFriend(Integer id, Integer friendId) {
        User user = UserUtils.getUserOrThrow(userStorage, id);
        User friend = UserUtils.getUserOrThrow(userStorage, friendId);
        /*Проверяем являются ли пользователи друзьями
        бросаем исключение, если да*/
        user.getFriends().add(friendId);
        friend.getFriends().add(id);
    }

    public void removeFriend(Integer id, Integer friendId) {
        User user = UserUtils.getUserOrThrow(userStorage, id);
        User friend = UserUtils.getUserOrThrow(userStorage, friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(id);
    }

    public List<User> getMutualFriends(Integer id, Integer friendId) {
        User user = UserUtils.getUserOrThrow(userStorage, id);
        User friend = UserUtils.getUserOrThrow(userStorage, friendId);

        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(integer -> UserUtils.getUserOrThrow(userStorage, integer))
                .toList();
    }

    public User getUserById(Integer id) {
        return UserUtils.getUserOrThrow(userStorage, id);
    }

    public List<User> getUserFriends(Integer id) {
        User user = UserUtils.getUserOrThrow(userStorage, id);
        return user.getFriends().stream()
                .map(integer -> UserUtils.getUserOrThrow(userStorage, integer)).toList();
    }
}
