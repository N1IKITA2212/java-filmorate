package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public Optional<Film> getFilm(int filmId) {
        return Optional.ofNullable(films.get(filmId));
    }

    @Override
    public List<Film> getAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film addFilm(Film film) {
        film.setId(getNewId());
        log.info("Фильму установлен id {}", film.getId());
        films.put(film.getId(), film);
        log.info("В коллекцию добавлен фильм {}", film);
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.error("Ошибка обновления фильма. В запросе не передан id");
            throw new NotEnoughDataException("Не заполнено поле id", "id");
        }
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
