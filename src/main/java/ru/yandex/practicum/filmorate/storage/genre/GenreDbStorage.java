package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса GenreStorage для работы с таблицей genres в базе данных.
 * Поддерживает получение всех жанров и получение жанра по идентификатору.
 */
@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {

    /**
     * SQL-запрос для получения всех жанров
     */
    private static final String GET_ALL_GENRES = """
            SELECT
            id,
            name
            FROM genres
            """;

    /**
     * SQL-запрос для получения жанра по id
     */
    private static final String GET_GENRE_BY_ID = """
            SELECT
            id,
            name
            FROM genres
            WHERE id = ?
            """;

    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> genreRowMapper;

    /**
     * Получает список всех жанров.
     *
     * @return список объектов Genre
     */
    @Override
    public List<Genre> getGenres() {
        return jdbc.query(GET_ALL_GENRES, genreRowMapper);
    }

    /**
     * Получает жанр по идентификатору.
     *
     * @param id идентификатор жанра
     * @return Optional с объектом Genre, если найден; иначе пустой Optional
     */
    @Override
    public Optional<Genre> getGenreById(int id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(GET_GENRE_BY_ID, genreRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
