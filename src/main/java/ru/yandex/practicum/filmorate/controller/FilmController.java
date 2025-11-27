package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.PostFilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

/**
 * REST-контроллер для управления фильмами.
 * Предоставляет операции создания, обновления, получения фильмов,
 * а также функциональность лайков и выборки популярных фильмов.
 * <p>
 * Базовый URL: /films
 */
@RestController
@RequestMapping("films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    /**
     * Возвращает список всех фильмов.
     * <p>
     * GET /films
     *
     * @return список DTO фильмов
     */
    @GetMapping
    public List<FilmDto> getFilms() {
        return filmService.getAllFilms();
    }

    /**
     * Создает новый фильм.
     * <p>
     * POST /films
     *
     * @param requestDto DTO с данными для создания фильма
     * @return созданный фильм
     */
    @PostMapping
    public FilmDto postFilm(@Valid @RequestBody PostFilmRequestDto requestDto) {
        return filmService.addFilm(requestDto);
    }

    /**
     * Обновляет данные существующего фильма.
     * <p>
     * PUT /films
     *
     * @param updateFilmRequestDto DTO с обновлёнными данными фильма
     * @return обновленный фильм
     */
    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequestDto updateFilmRequestDto) {
        return filmService.updateFilm(updateFilmRequestDto);
    }

    /**
     * Возвращает фильм по его идентификатору.
     * <p>
     * GET /films/{filmId}
     *
     * @param filmId идентификатор фильма
     * @return фильм, если найден
     */
    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    /**
     * Добавляет лайк фильму от указанного пользователя.
     * <p>
     * PUT /films/{id}/like/{userId}
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    /**
     * Удаляет лайк фильма от указанного пользователя.
     * <p>
     * DELETE /films/{id}/like/{userId}
     *
     * @param id     идентификатор фильма
     * @param userId идентификатор пользователя
     */
    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    /**
     * Возвращает самые популярные фильмы по количеству лайков.
     * <p>
     * GET /films/popular?count=N
     *
     * @param count количество фильмов (по умолчанию 10)
     * @return список наиболее популярных фильмов
     */
    @GetMapping("/popular")
    public List<FilmDto> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostLikedFilms(count);
    }
}
