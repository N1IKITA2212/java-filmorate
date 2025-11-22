package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmStorage {

    List<Film> getAllFilms();

    Film addFilm(Film film);

    Film updateFilm(Film film);

    Optional<Film> getFilm(int filmId);

    boolean isFilmPresent(Integer id);

    public void addGenresForFilm(int filmId, Set<Integer> genresId);

    public List<String> getFilmGenre(int filmId);

    public String getFilmRating(int filmId);

    public List<String> getUsersNamesLikedFilm(int filmId);

    public void updateFilmGenre(int filmId, Set<Integer> genresId);

    public void addLike(int filmId, int userId);

    public void removeLike(int filmId, int userId);
}
