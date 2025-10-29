package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.utils.FilmUtils;
import ru.yandex.practicum.filmorate.utils.UserUtils;

import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    public void addLike(Integer filmId, Integer userId) {
        Film film = FilmUtils.getFilmOrThrow(filmStorage, filmId);
        User user = UserUtils.getUserOrThrow(userStorage, userId);

        film.getLikes().add(userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        Film film = FilmUtils.getFilmOrThrow(filmStorage, filmId);
        User user = UserUtils.getUserOrThrow(userStorage, userId);

        film.getLikes().remove(userId);
    }

    public List<Film> getMostLikedFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count).toList();
    }

    public Film getFilmById(Integer id) {
        return FilmUtils.getFilmOrThrow(filmStorage, id);
    }
}
