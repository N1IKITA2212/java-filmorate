package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@Getter
public enum Genre {
    Comedy(1),
    Action(2),
    Adventure(3),
    Drama(4),
    Fantasy(5),
    Historical(6),
    Horror(7),
    Melodrama(8);


    private final int id;

    Genre(int id) {
        this.id = id;
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
