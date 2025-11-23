package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

/**
 * Перечисление жанров фильмов.
 * Каждому жанру сопоставлен уникальный идентификатор и название.
 * Используется для сериализации/десериализации JSON как объект.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Genre {

    Comedy(1, "Комедия"),
    Drama(2, "Драма"),
    Cartoon(3, "Мультфильм"),
    Thriller(4, "Триллер"),
    Documentary(5, "Документальный"),
    Action(6, "Боевик");


    private final int id;
    private final String name;

    /**
     * Конструктор жанра.
     *
     * @param id   уникальный идентификатор жанра
     * @param name название жанра
     */
    Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    /**
     * Получить идентификатор жанра.
     *
     * @return id жанра
     */
    public int getId() {
        return id;
    }

    /**
     * Получить название жанра.
     *
     * @return название жанра
     */
    public String getName() {
        return name;
    }

    /**
     * Получить жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return объект Genre
     * @throws NotFoundException если жанр с указанным id не найден
     */
    public static Genre getGenreById(int id) {
        for (Genre genre : Genre.values()) {
            if (genre.id == id) {
                return genre;
            }
        }
        throw new NotFoundException("Неизвестный id жанра " + id);
    }
}
