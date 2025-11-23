package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO для передачи информации о пользователе через REST API.
 * Содержит основные данные о пользователе и список друзей.
 */
@Data
public class UserDto {

    /**
     * Имя пользователя
     */
    private String name;

    /**
     * Электронная почта пользователя
     */
    private String email;

    /**
     * Логин пользователя
     */
    private String login;

    /**
     * Список email друзей пользователя
     */
    private List<String> emailFriends;

    /**
     * Дата рождения пользователя
     */
    private LocalDate birthday;

    /**
     * Уникальный идентификатор пользователя
     */
    private Integer id;
}
