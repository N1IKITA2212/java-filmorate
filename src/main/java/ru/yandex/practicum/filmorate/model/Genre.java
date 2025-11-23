package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Genre {
    Comedy(1, "Комедия"),
    Drama(2, "Драма"),
    Cartoon(3, "Мультфильм"),
    Fantasy(4, "Фантастика"),
    Historical(5, "Исторический"),
    Horror(6, "Ужасы"),
    Melodrama(7, "Мелодрама"),
    Adventure(8, "Приключения");


    private final int id;
    private final String name;

    Genre(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Genre getGenreById(int id) {
        for (Genre genre : Genre.values()) {
            if (genre.id == id) {
                return genre;
            }
        }
        throw new NotFoundException("Неизвестный id жанра " + id);
    }
}
