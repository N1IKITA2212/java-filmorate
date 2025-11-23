package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Модель фильма.
 * Используется для хранения информации о фильмах внутри приложения.
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class Film {

    /**
     * Множество идентификаторов пользователей, поставивших лайк фильму
     */
    private Set<Integer> likes = new HashSet<>();

    /**
     * Уникальный идентификатор фильма
     */
    private Integer id;

    /**
     * Название фильма. Не может быть пустым
     */
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    /**
     * Описание фильма. Не может быть пустым, максимум 200 символов
     */
    @NotBlank(message = "Не указано описание")
    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;

    /**
     * Продолжительность фильма в минутах. Должна быть положительной
     */
    @NotNull(message = "Не указана продолжительность")
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    /**
     * Дата выхода фильма. Проверяется кастомной аннотацией @ValidReleaseDate
     */
    @NotNull(message = "Дата выхода не указана")
    @ValidReleaseDate
    private LocalDate releaseDate;

    /**
     * Список жанров фильма
     */
    private List<Genre> genres = new ArrayList<>();

    /**
     * Рейтинг MPA фильма
     */
    private Mpa mpa;
}
