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

@RestController
@RequestMapping("films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;

    @GetMapping
    public List<FilmDto> getFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public FilmDto postFilm(@Valid @RequestBody PostFilmRequestDto requestDto) {
        return filmService.addFilm(requestDto);
    }

    @PutMapping
    public FilmDto updateFilm(@Valid @RequestBody UpdateFilmRequestDto updateFilmRequestDto) {
        return filmService.updateFilm(updateFilmRequestDto);
    }

    @GetMapping("/{filmId}")
    public FilmDto getFilmById(@PathVariable Integer filmId) {
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Integer id, @PathVariable Integer userId) {
        filmService.removeLike(id, userId);
    }

    @GetMapping("/popular")
    public List<FilmDto> getMostLikedFilms(@RequestParam(defaultValue = "10") int count) {
        return filmService.getMostLikedFilms(count);
    }
}
