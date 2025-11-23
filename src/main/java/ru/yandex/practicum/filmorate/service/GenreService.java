package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

/**
 * Сервис для работы с жанрами фильмов.
 * Предоставляет методы для получения всех жанров и получения жанра по идентификатору.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GenreService {

    private final GenreStorage genreStorage;

    /**
     * Получить список всех жанров фильмов.
     *
     * @return список объектов Genre
     */
    public List<Genre> getAllGenres() {
        return genreStorage.getGenres();
    }

    /**
     * Получить жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return объект Genre
     * @throws NotFoundException если жанр с указанным id не найден
     */
    public Genre getGenreById(int id) {
        return genreStorage.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден"));
    }
}
