package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.UserMapper;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    public List<UserDto> getMutualFriends(Integer id, Integer friendId) {
        User user = getUserOrThrow(id);
        User friend = getUserOrThrow(friendId);

        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(this::getUserDtoOrThrow)
                .toList();
    }

    public UserDto getUserById(Integer id) {
        return getUserDtoOrThrow(id);
    }

    public List<UserDto> getUserFriends(Integer id) {
        User user = getUserOrThrow(id);
        return user.getFriends().stream()
                .map(this::getUserDtoOrThrow).toList();
    }

    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(user -> {
                    List<String> friendsEmails = userStorage.getFriendsEmails(user.getId());
                    return userMapper.toDto(user, friendsEmails);
                })
                .collect(Collectors.toList());
    }

    public UserDto addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userMapper.toDto(userStorage.addUser(user), new ArrayList<>());
    }

    public UserDto updateUser(User user) {
        if (user.getId() == null) {
            log.error("Поле с ID пустое");
            throw new NotEnoughDataException("Не заполнено поле id", "id");
        }
        if (userStorage.isUserPresent(user.getId())) {
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            List<String> friendsEmails = userStorage.getFriendsEmails(user.getId());
            return userMapper.toDto(userStorage.updateUser(user), friendsEmails);
        }
        log.error("Пользователь с id {} не найден", user.getId());
        throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }

    private UserDto getUserDtoOrThrow(Integer id) {
        User user = userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        List<String> friendsEmails = userStorage.getFriendsEmails(id);
        return userMapper.toDto(user, friendsEmails);
    }

    private User getUserOrThrow(int id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}

