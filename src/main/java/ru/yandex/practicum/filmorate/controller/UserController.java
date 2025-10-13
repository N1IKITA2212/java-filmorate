package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadFormatDataException;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    private Integer getNewId() {
        Integer currentId = users.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++currentId;
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User postUser(@RequestBody User user) {
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            throw new BadFormatDataException("Email не может быть пустым и должен содержать @");
        }
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            throw new BadFormatDataException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            throw new BadFormatDataException("Дата рождения не может быть в будущем");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User putUser(@RequestBody User user) {
        if (user.getId() == null) {
            throw new NotEnoughDataException("Отсутствует ID");
        }
        if (users.containsKey(user.getId())) {
            User oldUser = users.get(user.getId());
            if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
                throw new BadFormatDataException("Email не может быть пустым и должен содержать @");
            }
            if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
                throw new BadFormatDataException("Логин не может быть пустым и содержать пробелы");
            }
            if (user.getBirthday().isAfter(LocalDate.now())) {
                throw new BadFormatDataException("Дата рождения не может быть в будущем");
            }
            oldUser.setEmail(user.getEmail());
            oldUser.setName(user.getName());
            oldUser.setBirthday(user.getBirthday());
            oldUser.setLogin(user.getLogin());
            return oldUser;
        }
        throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }
}
