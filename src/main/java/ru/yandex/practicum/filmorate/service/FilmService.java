package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Integer filmId, Integer userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        film.getLikes().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = getFilmOrThrow(filmId);
        User user = getUserOrThrow(userId);

        film.getLikes().remove(userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count).toList();
    }

    public Film getFilmById(Integer id) {
        return getFilmOrThrow(id);
    }

    public List<Film> getAllFilms() {
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) {
        return filmStorage.addFilm(film);
    }

    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.error("Ошибка обновления фильма. В запросе не передан id");
            throw new NotEnoughDataException("Не заполнено поле id", "id");
        }
        if (filmStorage.isFilmPresent(film.getId())) {
            return filmStorage.updateFilm(film);
        }
        log.error("Фильм с id {} не найден", film.getId());
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }

    private Film getFilmOrThrow(Integer id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    private User getUserOrThrow(Integer id) {
        return userStorage.getUser(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }
}
