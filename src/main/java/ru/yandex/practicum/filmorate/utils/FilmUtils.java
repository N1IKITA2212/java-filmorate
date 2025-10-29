package ru.yandex.practicum.filmorate.utils;

import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

public final class FilmUtils {

    private FilmUtils() {

    }

    public static Film getFilmOrThrow(FilmStorage filmStorage, Integer id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }
}
