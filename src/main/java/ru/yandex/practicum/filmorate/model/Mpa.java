package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import ru.yandex.practicum.filmorate.exception.NotFoundException;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Mpa {
    G(1, "G"),
    PG(2, "PG"),
    PG_13(3, "PG-13"),
    R(4, "R"),
    NC_17(5, "NC-17");

    private final Integer id;
    private final String name;

    Mpa(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Mpa getMpaById(int id) {
        for (Mpa mpa : Mpa.values()) {
            if (mpa.id.equals(id)) {
                return mpa;
            }
        }
        throw new NotFoundException("Неизвестный id mpa " + id);
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
