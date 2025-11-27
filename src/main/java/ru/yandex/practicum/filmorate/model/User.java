package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Модель пользователя.
 * Содержит информацию о пользователе, его друзьях и валидацию полей.
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class User {

    /**
     * Множество идентификаторов друзей пользователя
     */
    private Set<Integer> friends = new HashSet<>();

    /**
     * Уникальный идентификатор пользователя
     */
    private Integer id;

    /**
     * Электронная почта пользователя. Обязательное поле с валидацией email
     */
    @Email(message = "Не соответствует Email")
    @NotBlank(message = "Email не может быть пустым")
    private String email;

    /**
     * Логин пользователя. Не может быть пустым и не должен содержать пробелы
     */
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;

    /**
     * Имя пользователя. Может быть пустым
     */
    private String name;

    /**
     * Дата рождения пользователя. Не может быть null и не может быть в будущем
     */
    @NotNull
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
