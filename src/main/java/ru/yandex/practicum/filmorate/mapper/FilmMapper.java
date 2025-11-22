package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.film.PostFilmRequestDto;
import ru.yandex.practicum.filmorate.dto.film.UpdateFilmRequestDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class FilmMapper {

    public FilmDto toDto(Film film, String rating, List<String> genres, List<String> likes) {
        FilmDto filmDto = new FilmDto();
        filmDto.setId(film.getId());
        filmDto.setName(film.getName());
        filmDto.setDescription(film.getDescription());
        filmDto.setReleaseDate(film.getReleaseDate());
        filmDto.setDuration(film.getDuration());
        filmDto.setMpa(rating);
        filmDto.setGenres(genres);
        filmDto.setLikes(likes);

        return filmDto;
    }

    public Film toFilmFromPostRequestDto(PostFilmRequestDto postFilmRequestDto) {
        Film film = new Film();
        film.setName(postFilmRequestDto.getName());
        film.setDuration(postFilmRequestDto.getDuration());
        film.setDescription(postFilmRequestDto.getDescription());
        film.setReleaseDate(postFilmRequestDto.getReleaseDate());

        film.setMpa(RatingMpa.getMpaById(postFilmRequestDto.getMpa().getId()));
        List<Genre> genres = postFilmRequestDto.getGenres().stream()
                .map(PostFilmRequestDto.GenreRequest::getId)
                .map(Genre::getGenreById)
                .collect(Collectors.toList());
        film.setGenres(genres);
        return film;
    }

    public Film toFilmFromUpdateRequestDto(UpdateFilmRequestDto updateFilmRequestDto) {
        Film film = new Film();
        film.setId(updateFilmRequestDto.getId());
        film.setName(updateFilmRequestDto.getName());
        film.setDuration(updateFilmRequestDto.getDuration());
        film.setDescription(updateFilmRequestDto.getDescription());
        film.setReleaseDate(updateFilmRequestDto.getReleaseDate());

        film.setMpa(RatingMpa.getMpaById(updateFilmRequestDto.getMpa().getId()));
        List<Genre> genres = updateFilmRequestDto.getGenres().stream()
                .map(PostFilmRequestDto.GenreRequest::getId)
                .map(Genre::getGenreById)
                .collect(Collectors.toList());
        film.setGenres(genres);
        return film;
    }

}
