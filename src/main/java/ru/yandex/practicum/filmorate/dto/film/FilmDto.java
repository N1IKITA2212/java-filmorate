package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;

/**
 * DTO, представляющий данные фильма для ответов API.
 * Используется для передачи полной информации о фильме,
 * включая базовые атрибуты, жанры, рейтинг и лайки пользователей.
 */
@Data
public class FilmDto {

    /**
     * Уникальный идентификатор фильма.
     */
    private Integer id;

    /**
     * Название фильма.
     */
    private String name;

    /**
     * Описание фильма.
     */
    private String description;

    /**
     * Продолжительность фильма в минутах.
     */
    private Integer duration;

    /**
     * Дата выхода фильма.
     */
    private LocalDate releaseDate;

    /**
     * Рейтинг возрастного ограничения (например, PG-13, R).
     */
    private Mpa mpa;

    /**
     * Список жанров фильма.
     */
    private List<Genre> genres;

    /**
     * Список email пользователей, поставивших лайк фильму.
     */
    private List<String> likes;
}
