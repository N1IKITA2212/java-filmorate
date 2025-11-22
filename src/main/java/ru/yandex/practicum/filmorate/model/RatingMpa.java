package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import ru.yandex.practicum.filmorate.exception.NotFoundException;


@Getter
public enum RatingMpa {
    G(1),
    PG(2),
    PG_13(3),
    R(4),
    NC_17(5);

    private final Integer id;

    RatingMpa(Integer id) {
        this.id = id;
    }

    public static RatingMpa getMpaById(int id) {
        for (RatingMpa ratingMpa : RatingMpa.values()) {
            if (ratingMpa.id.equals(id)) {
                return ratingMpa;
            }
        }
        throw new NotFoundException("Неизвестный id mpa " + id);
    }
}
