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

    /**
     * Добавляет лайк фильму от пользователя.
     *
     * <p>Шаги:
     * <ul>
     * <li> Проверяет наличие фильма в хранилище по идентификатору. Если фильм отсутствует, выбрасывает NotFoundException.</li>
     * <li> Проверяет наличие пользователя в хранилище по идентификатору. Если пользователь отсутствует, выбрасывает NotFoundException.</li>
     * <li> Добавляет лайк фильму от указанного пользователя в хранилище.</li>
     * </ul>
     *
     * @param filmId идентификатор фильма, которому добавляется лайк
     * @param userId идентификатор пользователя, добавляющего лайк
     * @throws NotFoundException если фильм или пользователь с указанным идентификатором не найдены
     */
    public void addLike(Integer filmId, Integer userId) {
        if (!filmStorage.isFilmPresent(filmId)) {
            throw new NotFoundException("Фильм с id " + filmId + " не найден");
        }
        if (!userStorage.isUserPresent(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }
        filmStorage.addLike(filmId, userId);
    }

    /**
     * Удаляет лайк у фильма от пользователя.
     *
     * <p>Шаги:
     * <ul>
     * <li> Проверяет наличие фильма в хранилище по идентификатору. Если фильм отсутствует, выбрасывает NotFoundException.</li>
     * <li> Проверяет наличие пользователя в хранилище по идентификатору. Если пользователь отсутствует, выбрасывает NotFoundException.</li>
     * <li> Проверяет, поставил ли пользователь лайк фильму. Если лайк существует, удаляет его.</li>
     * </ul>
     *
     * @param filmId идентификатор фильма, у которого удаляется лайк
     * @param userId идентификатор пользователя, удаляющего лайк
     * @throws NotFoundException если фильм или пользователь с указанным идентификатором не найдены
     */
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

    /**
     * Возвращает список самых популярных фильмов в виде DTO.
     *
     * <p>Шаги:
     * <ul>
     * <li> Получает список всех фильмов из хранилища.</li>
     * <li> Сортирует фильмы по количеству лайков в порядке убывания.</li>
     * <li> Ограничивает количество фильмов до указанного значения {@code count}.</li>
     * <li> Преобразует каждый фильм в DTO с использованием метода {@code getFilmDtoOrThrow}.</li>
     * <li> Собирает преобразованные DTO в список и возвращает его.</li>
     * </ul>
     *
     * @param count максимальное количество фильмов в списке
     * @return список самых популярных фильмов в виде объектов {@code FilmDto}
     */
    public List<FilmDto> getMostLikedFilms(int count) {
        return filmStorage.getAllFilms().stream()
                .sorted(Comparator.comparing((Film film) -> film.getLikes().size()).reversed())
                .limit(count)
                .map(film -> getFilmDtoOrThrow(film.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Возвращает DTO фильма по его идентификатору.
     *
     * <p>Шаги:
     * <ul>
     * <li> Вызывает метод {@code getFilmDtoOrThrow}, который проверяет наличие фильма и преобразует его в DTO.</li>
     * </ul>
     *
     * @param id идентификатор фильма
     * @return DTO фильма с заполненными полями
     * @throws NotFoundException если фильм с указанным идентификатором не найден
     */
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

    /**
     * Добавляет новый фильм в хранилище и возвращает его DTO.
     *
     * <p>Шаги:
     * <ul>
     * <li> Преобразует входной DTO запроса в сущность фильма с использованием FilmMapper.</li>
     * <li> Добавляет фильм в хранилище и получает добавленную сущность фильма.</li>
     * <li> Извлекает идентификаторы жанров добавленного фильма и добавляет их в хранилище жанров для фильма.</li>
     * <li> Преобразует жанры из запроса в список объектов Genre, используя GenreStorage.</li>
     * <li> Получает объект MPA из хранилища по идентификатору из запроса.</li>
     * <li> Преобразует добавленную сущность фильма в DTO с заполненными полями жанров и рейтинга.</li>
     * </ul>
     *
     * @param postFilmRequestDto DTO запроса для добавления нового фильма
     * @return DTO добавленного фильма с заполненными полями жанров и рейтинга
     */
    public FilmDto addFilm(PostFilmRequestDto postFilmRequestDto) {

        Film addedFilm = filmStorage.addFilm(filmMapper.toFilmFromPostRequestDto(postFilmRequestDto));
        Set<Integer> genresId = addedFilm.getGenres().stream().map(Genre::getId).collect(Collectors.toSet());
        filmStorage.addGenresForFilm(addedFilm.getId(), genresId);
        List<Genre> addedFilmGenres = postFilmRequestDto.getGenres().stream()
                .map(PostFilmRequestDto.GenreRequest::getId)
                .map(genreStorage::getGenreById)
                .map(genreOpt -> genreOpt.orElseThrow(() -> new NotFoundException("Переданных жанров нет в базе")))
                .collect(Collectors.toList());
        Mpa addedFilmMpa = mpaStorage.getMpaById(postFilmRequestDto.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с таким id не существует"));
        return filmMapper.toDto(addedFilm, addedFilmMpa, addedFilmGenres, new ArrayList<>());
    }

    /**
     * Обновляет данные фильма и возвращает его DTO.
     *
     * <p>Шаги:
     * <ul>
     * <li> Проверяет, передан ли идентификатор фильма в запросе. Если идентификатор отсутствует, выбрасывает NotEnoughDataException.</li>
     * <li> Преобразует DTO запроса в сущность фильма с использованием FilmMapper.</li>
     * <li> Проверяет наличие фильма в хранилище по идентификатору. Если фильм отсутствует, выбрасывает NotFoundException.</li>
     * <li> Обновляет данные фильма в хранилище.</li>
     * <li> Извлекает идентификаторы жанров обновленного фильма и обновляет их в хранилище жанров для фильма.</li>
     * <li> Преобразует жанры из запроса в список объектов Genre, используя GenreStorage.</li>
     * <li> Получает объект MPA из хранилища по идентификатору из запроса.</li>
     * <li> Получает список имен пользователей, поставивших лайк фильму.</li>
     * <li> Преобразует обновленную сущность фильма в DTO с заполненными полями жанров, рейтинга и лайков.</li>
     * </ul>
     *
     * @param updateFilmRequestDto DTO запроса для обновления данных фильма
     * @return DTO обновленного фильма с заполненными полями жанров, рейтинга и лайков
     * @throws NotEnoughDataException если идентификатор фильма отсутствует в запросе
     * @throws NotFoundException      если фильм с указанным идентификатором не найден
     */
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
                    .map(genreOpt -> genreOpt.orElseThrow(() -> new NotFoundException("Переданных жанров нет в базе")))
                    .collect(Collectors.toList());
            Mpa updatedFilmMpa = mpaStorage.getMpaById(updateFilmRequestDto.getMpa().getId())
                    .orElseThrow(() -> new NotFoundException("Рейтинг с таким id не существует"));
            List<String> updatedFilmLikes = filmStorage.getUsersNamesLikedFilm(film.getId());
            return filmMapper.toDto(updatedFilm, updatedFilmMpa, updatedFilmGenres, updatedFilmLikes);
        }
        log.error("Фильм с id {} не найден", film.getId());
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }

    /**
     * Возвращает сущность фильма по его идентификатору или выбрасывает исключение, если фильм не найден.
     *
     * @param id идентификатор фильма
     * @return сущность фильма
     * @throws NotFoundException если фильм с указанным идентификатором не найден
     */
    private Film getFilmOrThrow(Integer id) {
        return filmStorage.getFilm(id)
                .orElseThrow(() -> new NotFoundException("Фильм с id " + id + " не найден"));
    }

    /**
     * Возвращает сущность пользователя по его идентификатору или выбрасывает исключение, если пользователь не найден.
     *
     * @param id идентификатор пользователя
     * @return сущность пользователя
     * @throws NotFoundException если пользователь с указанным идентификатором не найден
     */
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
        Mpa mpa = mpaStorage.getMpaById(film.getMpa().getId())
                .orElseThrow(() -> new NotFoundException("Рейтинг с таким id не существует"));
        List<Genre> genres = film.getGenres();
        List<String> likes = filmStorage.getUsersNamesLikedFilm(id);
        return filmMapper.toDto(film, mpa, genres, likes);
    }
}
