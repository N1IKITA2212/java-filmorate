package ru.yandex.practicum.filmorate.storage.genre;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GenreDbStorage implements GenreStorage {
    private static final String GET_ALL_GENRES = """
            SELECT
            id,
            name
            FROM genres
            """;
    private static final String GET_GENRE_BY_ID = """
            SELECT
            id,
            name
            FROM genres
            WHERE id = ?
            """;

    private final JdbcTemplate jdbc;
    private final RowMapper<Genre> genreRowMapper;

    @Override
    public List<Genre> getGenres() {
        return jdbc.query(GET_ALL_GENRES, genreRowMapper);
    }

    @Override
    public Genre getGenreById(int id) {
        return jdbc.queryForObject(GET_GENRE_BY_ID, genreRowMapper, id);
    }
}
