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

/**
 * Сервис для работы с пользователями.
 * Реализует бизнес-логику, включая получение пользователей, их друзей и взаимных друзей.
 */
@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;
    private final UserMapper userMapper;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage, UserMapper userMapper) {
        this.userStorage = userStorage;
        this.userMapper = userMapper;
    }

    /**
     * Получить список взаимных друзей между двумя пользователями.
     *
     * @param id       идентификатор первого пользователя
     * @param friendId идентификатор второго пользователя
     * @return список DTO взаимных друзей
     */
    public List<UserDto> getMutualFriends(Integer id, Integer friendId) {
        User user = getUserOrThrow(id);
        User friend = getUserOrThrow(friendId);

        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(this::getUserDtoOrThrow)
                .toList();
    }

    /**
     * Получить пользователя по идентификатору.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     */
    public UserDto getUserById(Integer id) {
        return getUserDtoOrThrow(id);
    }

    /**
     * Получить список друзей пользователя.
     *
     * @param id идентификатор пользователя
     * @return список DTO друзей
     */
    public List<UserDto> getUserFriends(Integer id) {
        User user = getUserOrThrow(id);
        return user.getFriends().stream()
                .map(this::getUserDtoOrThrow)
                .toList();
    }

    /**
     * Получить список всех пользователей.
     *
     * @return список DTO всех пользователей
     */
    public List<UserDto> getAllUsers() {
        return userStorage.getAllUsers().stream()
                .map(user -> {
                    List<String> friendsEmails = userStorage.getFriendsEmails(user.getId());
                    return userMapper.toDto(user, friendsEmails);
                })
                .collect(Collectors.toList());
    }

    /**
     * Добавить нового пользователя.
     * Если имя не указано, используется логин в качестве имени.
     *
     * @param user объект пользователя для добавления
     * @return DTO добавленного пользователя
     */
    public UserDto addUser(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userMapper.toDto(userStorage.addUser(user), new ArrayList<>());
    }

    /**
     * Обновить существующего пользователя.
     * Проверяет наличие ID и существование пользователя.
     *
     * @param user объект пользователя с обновленными данными
     * @return DTO обновленного пользователя
     */
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

    /**
     * Получить DTO пользователя по ID или выбросить исключение, если пользователь не найден.
     *
     * @param id идентификатор пользователя
     * @return DTO пользователя
     */
    private UserDto getUserDtoOrThrow(Integer id) {
        User user = userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        List<String> friendsEmails = userStorage.getFriendsEmails(id);
        return userMapper.toDto(user, friendsEmails);
    }

    /**
     * Получить пользователя по ID или выбросить исключение, если пользователь не найден.
     *
     * @param id идентификатор пользователя
     * @return объект User
     */
    private User getUserOrThrow(int id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
