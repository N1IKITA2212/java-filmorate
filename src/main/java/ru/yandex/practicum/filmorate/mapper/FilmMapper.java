package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.PostFilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper для преобразования между сущностью Film и DTO.
 * Обеспечивает конвертацию как в сторону DTO, так и из запросов Post/Update в модель.
 */
@Component
public class FilmMapper {

    /**
     * Преобразует объект Film в DTO FilmDto.
     *
     * @param film   объект модели фильма
     * @param mpa    рейтинг MPA фильма
     * @param genres список жанров фильма
     * @param likes  список email пользователей, поставивших лайк
     * @return FilmDto с заполненными полями
     */
    public FilmDto toDto(Film film, Mpa mpa, List<Genre> genres, List<String> likes) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(mpa);
        filmDto.setGenres(genres);
        filmDto.setLikes(likes);

        return filmDto;
    }

    /**
     * Преобразует PostFilmRequestDto в объект Film.
     * Используется при создании нового фильма.
     *
     * @param postFilmRequestDto DTO запроса на создание фильма
     * @return объект Film с заполненными полями
     */
    public Film toFilmFromPostRequestDto(PostFilmRequestDto postFilmRequestDto) {
        Film film = new Film();
        film.setName(postFilmRequestDto.getName());
        film.setDuration(postFilmRequestDto.getDuration());
        film.setDescription(postFilmRequestDto.getDescription());
        film.setReleaseDate(postFilmRequestDto.getReleaseDate());

        film.setMpa(Mpa.getMpaById(postFilmRequestDto.getMpa().getId()));
        List<Genre> genres = postFilmRequestDto.getGenres().stream()
                .map(PostFilmRequestDto.GenreRequest::getId)
                .map(Genre::getGenreById)
                .collect(Collectors.toList());
        film.setGenres(genres);
        return film;
    }

    /**
     * Преобразует UpdateFilmRequestDto в объект Film.
     * Используется при обновлении существующего фильма.
     *
     * @param updateFilmRequestDto DTO запроса на обновление фильма
     * @return объект Film с заполненными полями
     */
    public Film toFilmFromUpdateRequestDto(UpdateFilmRequestDto updateFilmRequestDto) {
        Film film = new Film();
        film.setId(updateFilmRequestDto.getId());
        film.setName(updateFilmRequestDto.getName());
        film.setDuration(updateFilmRequestDto.getDuration());
        film.setDescription(updateFilmRequestDto.getDescription());
        film.setReleaseDate(updateFilmRequestDto.getReleaseDate());

        film.setMpa(Mpa.getMpaById(updateFilmRequestDto.getMpa().getId()));
        List<Genre> genres = updateFilmRequestDto.getGenres().stream()
                .map(UpdateFilmRequestDto.GenreRequest::getId)
                .map(Genre::getGenreById)
                .collect(Collectors.toList());
        film.setGenres(genres);
        return film;
    }

}
