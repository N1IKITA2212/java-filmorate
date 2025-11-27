package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

/**
 * REST-контроллер для работы с жанрами фильмов.
 * Предоставляет эндпоинты для получения всех жанров и получения жанра по идентификатору.
 */
@RestController
@RequestMapping("genres")
@RequiredArgsConstructor
@Slf4j
public class GenreController {

    private final GenreService genreService;

    /**
     * Получить список всех жанров.
     *
     * @return список объектов Genre
     */
    @GetMapping
    public List<Genre> getAllGenres() {
        return genreService.getAllGenres();
    }

    /**
     * Получить жанр по его идентификатору.
     *
     * @param id идентификатор жанра
     * @return объект Genre
     */
    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) {
        return genreService.getGenreById(id);
    }
}
