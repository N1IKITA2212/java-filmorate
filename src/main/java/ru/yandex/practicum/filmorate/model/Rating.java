package ru.yandex.practicum.filmorate.model;

import lombok.Getter;


@Getter
public enum Rating {
    G(1),
    PG(2),
    PG_13(3),
    R(4),
    NC_17(5);

    private final Integer id;

    Rating(Integer id) {
        this.id = id;
    }

}
