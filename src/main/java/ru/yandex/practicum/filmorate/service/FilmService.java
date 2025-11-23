package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.PostFilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.exception.NotEnoughDataException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service

public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final FilmMapper filmMapper;
    private final MpaStorage mpaStorage;
    private final GenreStorage genreStorage;

    public FilmService(@Qualifier("filmDbStorage") FilmStorage filmStorage,
                       @Qualifier("userDbStorage") UserStorage userStorage,
                       MpaStorage mpaStorage, GenreStorage genreStorage,
                       FilmMapper filmMapper) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
        this.filmMapper = filmMapper;
        this.mpaStorage = mpaStorage;
        this.genreStorage = genreStorage;
    }

    public void addLike(Integer filmId, Integer userId) {
        if (!filmStorage.isFilmPresent(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (!userStorage.isUserPresent(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
    }

    public void removeLike(Integer filmId, Integer userId) {
        if (!filmStorage.isFilmPresent(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (!userStorage.isUserPresent(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        if (getFilmOrThrow(filmId).getLikes().contains(userId)) {
            filmStorage.removeLike(filmId, userId);
        }
    }

    public List<FilmDto> getMostLikedFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .map(film -> getFilmDtoOrThrow(film.getId()))
                .collect(Collectors.toList());
    }

    public FilmDto getFilmById(Integer id) {
        return getFilmDtoOrThrow(id);
    }

    /**
     * Возвращает список всех фильмов в виде DTO.
     *
     * <p>Шаги:
     * <ul>
     * <li> Получает список всех фильмов из хранилища.</li>
     * <li> Для каждого фильма вызывает метод {@code getFilmDtoOrThrow}, чтобы преобразовать сущность фильма в DTO.</li>
     * <li> Собирает преобразованные DTO в список и возвращает его.</li>
     * </ul>
     *
     * @return список всех фильмов в виде объектов {@code FilmDto}
     */
    public List<FilmDto> getAllFilms() {
        return filmStorage.getAllFilms().stream()
                .map(film -> getFilmDtoOrThrow(film.getId()))
                .collect(Collectors.toList());
    }

    public FilmDto addFilm(PostFilmRequestDto postFilmRequestDto) {

        Film addedFilm = filmStorage.addFilm(filmMapper.toFilmFromPostRequestDto(postFilmRequestDto));
        Set<Integer> genresId = addedFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        filmStorage.addGenresForFilm(addedFilm.getId(), genresId);
        List<Genre> addedFilmGenres = postFilmRequestDto.getGenres().stream()
                .map(PostFilmRequestDto.GenreRequest::getId)
                .map(genreStorage::getGenreById)
                .collect(Collectors.toList());
        Mpa addedFilmMpa = mpaStorage.getMpaById(postFilmRequestDto.getMpa().getId());
        return filmMapper.toDto(addedFilm, addedFilmMpa, addedFilmGenres, new ArrayList<>());
    }

    public FilmDto updateFilm(UpdateFilmRequestDto updateFilmRequestDto) {
        if (updateFilmRequestDto.getId() == null) {
            log.error("Ошибка обновления фильма. В запросе не передан id");
            throw new NotEnoughDataException("Не заполнено поле id", "id");
        }
        Film film = filmMapper.toFilmFromUpdateRequestDto(updateFilmRequestDto);
        if (filmStorage.isFilmPresent(film.getId())) {
            Film updatedFilm = filmStorage.updateFilm(film);
            Set<Integer> genresId = film.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
            filmStorage.updateFilmGenre(film.getId(), genresId);
            List<Genre> updatedFilmGenres = updateFilmRequestDto.getGenres().stream()
                    .map(UpdateFilmRequestDto.GenreRequest::getId)
                    .map(genreStorage::getGenreById)
                    .collect(Collectors.toList());
            Mpa updatedFilmMpa = mpaStorage.getMpaById(updateFilmRequestDto.getMpa().getId());
            List<String> updatedFilmLikes = filmStorage.getUsersNamesLikedFilm(film.getId());
            return filmMapper.toDto(updatedFilm, updatedFilmMpa, updatedFilmGenres, updatedFilmLikes);
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

    /**
     * Возвращает DTO фильма по его идентификатору или выбрасывает исключение, если фильм не найден.
     *
     * <p>Шаги:
     * <ul>
     * <li> Проверяет наличие фильма в хранилище по идентификатору. Если фильм отсутствует, выбрасывает NotFoundException.</li>
     * <li> Получает сущность фильма из хранилища. Если фильм отсутствует, выбрасывает NotFoundException.</li>
     * <li> Получает рейтинг фильма из хранилища.</li>
     * <li> Получает список жанров фильма из хранилища.</li>
     * <li> Получает список имён пользователей, поставивших лайк фильму.</li>
     * <li> Преобразует сущность фильма в DTO с использованием FilmMapper.</li>
     * </ul>
     *
     * @param id идентификатор фильма
     * @return DTO фильма с заполненными полями рейтинга, жанров и лайков
     * @throws NotFoundException если фильм с указанным идентификатором не найден
     */
    private FilmDto getFilmDtoOrThrow(Integer id) {
        if (!filmStorage.isFilmPresent(id)) {
            throw new NotFoundException("Фильм с id " + id + " не найден");
        }
        Film film = filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId());
        List<Genre> genres = film.getGenres();
        List<String> likes = filmStorage.getUsersNamesLikedFilm(id);
        return filmMapper.toDto(film, mpa, genres, likes);
    }
}
