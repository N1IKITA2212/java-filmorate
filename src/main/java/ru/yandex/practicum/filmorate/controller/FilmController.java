package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film postFilm(@Valid @RequestBody Film film) {
        film.setId(getNewId());
        log.info("Фильму установлен id {}", film.getId());
        films.put(film.getId(), film);
        log.info("В коллекцию добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        // Проверка передано ли ID в запросе
        if (film.getId() == null) {
            log.error("Не заполнено id");
            throw new NotEnoughDataException("Id должно быть заполнено");
        }
        // Проверка есть ли фильм с таким ID в программе
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Фильм с id {} обновлен", film.getId());
            return film;
        }
        log.error("Фильм с id {} не найден", film.getId());
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }

    private Integer getNewId() {
        Integer currentId = films.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++currentId;
    }
}
