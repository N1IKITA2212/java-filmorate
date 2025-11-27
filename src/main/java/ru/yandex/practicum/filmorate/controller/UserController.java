package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FriendshipService;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.List;

/**
 * REST-контроллер для работы с пользователями.
 * Поддерживает операции CRUD, управление друзьями и получение взаимных друзей.
 */
@RestController
@RequestMapping("users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final FriendshipService friendshipService;

    /**
     * Получить список всех пользователей.
     *
     * @return список DTO всех пользователей
     */
    @GetMapping
    public List<UserDto> getUsers() {
        return userService.getAllUsers();
    }

    /**
     * Добавить нового пользователя.
     *
     * @param user объект пользователя для добавления
     * @return DTO добавленного пользователя
     */
    @PostMapping
    public UserDto postUser(@Valid @RequestBody User user) {
        return userService.addUser(user);
    }

    /**
     * Обновить существующего пользователя.
     *
     * @param user объект пользователя с обновленными данными
     * @return DTO обновленного пользователя
     */
    @PutMapping
    public UserDto putUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    /**
     * Получить пользователя по идентификатору.
     *
     * @param userId идентификатор пользователя
     * @return DTO пользователя
     */
    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Integer userId) {
        return userService.getUserById(userId);
    }

    /**
     * Добавить друга пользователю.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @PutMapping("/{id}/friends/{friendId}")
    public void addFriends(@PathVariable Integer id, @PathVariable Integer friendId) {
        friendshipService.addFriend(id, friendId);
    }

    /**
     * Удалить друга пользователя.
     *
     * @param id       идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable Integer id, @PathVariable Integer friendId) {
        friendshipService.removeFriend(id, friendId);
    }

    /**
     * Получить список друзей пользователя.
     *
     * @param id идентификатор пользователя
     * @return список DTO друзей пользователя
     */
    @GetMapping("/{id}/friends")
    public List<UserDto> getUserFriends(@PathVariable Integer id) {
        return userService.getUserFriends(id);
    }

    /**
     * Получить список взаимных друзей между двумя пользователями.
     *
     * @param id      идентификатор первого пользователя
     * @param otherId идентификатор второго пользователя
     * @return список DTO взаимных друзей
     */
    @GetMapping("/{id}/friends/common/{otherId}")
    public List<UserDto> getMutualFriends(@PathVariable Integer id, @PathVariable Integer otherId) {
        return userService.getMutualFriends(id, otherId);
    }
}
