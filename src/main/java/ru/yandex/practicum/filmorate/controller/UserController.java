package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("users")
@Slf4j
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
    public User postUser(@Valid @RequestBody User user) {
        user.setId(getNewId());
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь с id {} добавлен ", user.getId());
        return user;
    }

    @PutMapping
    public User putUser(@Valid @RequestBody User user) {
        if (user.getId() == null) {
            log.error("Поле с ID пустое");
            throw new NotEnoughDataException("Отсутствует ID");
        }
        if (users.containsKey(user.getId())) {
            users.put(user.getId(), user);
            if (user.getName() == null || user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            log.info("Пользователь с id {} обновлен", user.getId());
            return user;
        }
        log.error("Пользователь с id {} не найден", user.getId());
        throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }
}
