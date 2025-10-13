package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.BadFormatDataException;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
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
    public Film postFilm(@RequestBody Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.error("Отсутствует название фильма");
            throw new NotEnoughDataException("Отсутствует название фильма");
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            log.error("Отсутствует описание фильма");
            throw new NotEnoughDataException("Отсутствует описание фильма");
        } else if (film.getDescription().length() > 200) {
            log.error("Длина описания превышает 200 символов");
            throw new BadFormatDataException("Длина описания превышает 200 символов");
        }
        if (film.getReleaseDate() == null) {
            log.error("Отсутствует дата выхода фильма");
            throw new NotEnoughDataException("Отсутствует дата выхода фильма");
        } else if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата выхода фильма не должна быть раньше чем 28 декабря 1895");
            throw new BadFormatDataException("Дата выхода фильма не должна быть раньше чем 28 декабря 1895");
        }
        if (film.getDuration() == null) {
            log.error("Отсутствует продолжительность фильма");
            throw new NotEnoughDataException("Отсутствует продолжительность фильма");
        } else if (film.getDuration() <= 0) {
            log.error("Продолжительность не может быть меньше нуля");
            throw new BadFormatDataException("Продолжительность не может быть меньше нуля");
        }
        film.setId(getNewId());
        log.info("Фильму установлен id {}", film.getId());
        films.put(film.getId(), film);
        log.info("В коллекцию добавлен фильм {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        // Проверка передано ли ID в запросе
        if (film.getId() == null) {
            log.error("Не заполнено id");
            throw new NotEnoughDataException("Id должно быть заполнено");
        }
        // Проверка есть ли фильм с таким ID в программе
        if (films.containsKey(film.getId())) {
            Film oldFilm = films.get(film.getId());
            // Валидация описания: если не null, то проверяем длину
            if (film.getDescription() != null) {
                if (film.getDescription().length() > 200) {
                    log.error("Длина описания превышает 200 символов");
                    throw new BadFormatDataException("Длина описания превышает 200 символов");
                }
                oldFilm.setDescription(film.getDescription());
                log.info("Фильму установлено новое описание {}", oldFilm.getDescription());
            }
            // Проверяем передано ли название фильма
            if (film.getName() != null && !film.getName().isBlank()) {
                oldFilm.setName(film.getName());
                log.info("Фильму установлено новое имя {}", oldFilm.getName());
            }
            // Проверяем дату выхода фильма
            if (film.getReleaseDate() != null) {
                if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
                    log.error("Дата выхода фильма раннее чем 28 декабря 1895");
                    throw new BadFormatDataException("Дата выхода фильма не должна быть раньше чем 28 декабря 1895");
                }
                oldFilm.setReleaseDate(film.getReleaseDate());
                log.info("Фильму изменена дата выхода {}", oldFilm.getReleaseDate());
            }
            if (film.getDuration() != null && film.getDuration() != 0) {
                oldFilm.setDuration(film.getDuration());
            }
            return oldFilm;
        }
        log.error("Фильм с id {} не найден", film.getId());
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }

    private Integer getNewId() {
        Integer currentId = films.keySet().stream().max(Integer::compareTo).orElse(0);
        return ++currentId;
    }
}
