package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final static String GET_ALL_FILMS_WITH_GENRES_AND_MPA = """
            SELECT
            f.id,
            f.name,
            f.description,
            f.duration,
            f.release_date,
            f.rating_id,
            g.id AS genre_id,
            fl.user_id AS like_id
            FROM films AS f
            LEFT JOIN film_genre AS fg ON f.id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.id
            LEFT JOIN film_likes AS fl ON f.id = fl.film_id
            """;
    private final static String INSERT_FILM_QUERY = "INSERT INTO films (name, description, duration, release_date," +
            " rating_id) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, duration = ?, " +
            "release_date = ?, rating_id = ? WHERE id = ?";
    private static final String GET_FILM_BY_ID_QUERY = """
            SELECT
            f.id,
            f.name,
            f.description,
            f.duration,
            f.release_date,
            f.rating_id,
            g.id AS genre_id,
            fl.user_id AS like_id
            FROM films AS f
            LEFT JOIN film_genre AS fg ON f.id = fg.film_id
            LEFT JOIN genres AS g ON fg.genre_id = g.id
            LEFT JOIN film_likes AS fl ON f.id = fl.film_id
            WHERE f.id = ?
            """;
    private static final String GET_USERS_NAME_LIKED_FILM = """
            SELECT u.name
            FROM users AS u
            JOIN film_likes AS fl ON u.id = fl.user_id
            WHERE fl.film_id = ?
            """;
    private static final String GET_FILM_GENRE = """
            SELECT g.name
            FROM genres AS g
            JOIN film_genre AS fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            """;
    private static final String GET_FILM_RATING = """
            SELECT r.name
            FROM ratings AS r
            JOIN films AS f ON r.id = f.rating_id
            WHERE f.id = ?
            """;
    private static final String INSERT_FILM_LIKE = """
            INSERT INTO film_likes (user_id, film_id)
            VALUES (?, ?)
            """;
    private static final String DELETE_FILM_LIKE = """
            DELETE FROM film_likes
            WHERE
            user_id = ? AND film_id = ?
            """;

    private static final String INSERT_GENRES_FOR_FILM = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";

    private static final String DELETE_GENRES_FOR_FILM = "DELETE from film_genre WHERE film_id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<Film> mapper;
    private final ResultSetExtractor<List<Film>> filmExtractor;

    @Override
    public List<Film> getAllFilms() {
        return jdbc.query(GET_ALL_FILMS_WITH_GENRES_AND_MPA, filmExtractor);
    }

    @Override
    public Film addFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_FILM_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, film.getName());
            ps.setString(2, film.getDescription());
            ps.setInt(3, film.getDuration());
            ps.setObject(4, film.getReleaseDate());
            ps.setInt(5, film.getMpa().getId());
            return ps;
        }, keyHolder);
        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            film.setId(id);
            return film;
        } else {
            throw new InternalServerException("Ошибка при добавлении фильма");
        }
    }

    @Override
    public Film updateFilm(Film film) {
        Object[] params = {film.getName(), film.getDescription(), film.getDuration(), film.getReleaseDate(),
                film.getMpa().getId(), film.getId()};
        int rowsUpdated = jdbc.update(UPDATE_FILM_QUERY, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Ошибка при обновлении фильма");
        }
        return getFilm(film.getId()).orElseThrow(() -> new InternalServerException("Фильм не найден после обновления"));
    }

    @Override
    public Optional<Film> getFilm(int filmId) {
        List<Film> films = jdbc.query(GET_FILM_BY_ID_QUERY, filmExtractor, filmId);
        if (films == null || films.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(films.get(0));
        }
    }

    public void addGenresForFilm(int filmId, Set<Integer> genresId) {
        jdbc.batchUpdate(INSERT_GENRES_FOR_FILM, genresId, genresId.size(),
                (ps, genreId) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genreId);
                });
    }

    @Override
    public List<String> getUsersNamesLikedFilm(int filmId) {
        return jdbc.query(GET_USERS_NAME_LIKED_FILM,
                (rs, rowNum) -> rs.getString("name"), filmId);
    }

    @Override
    public List<String> getFilmGenre(int filmId) {
        return jdbc.query(GET_FILM_GENRE,
                (rs, rowNum) -> rs.getString("name"), filmId);
    }

    @Override
    public String getFilmRating(int filmId) {
        return jdbc.queryForObject(GET_FILM_RATING,
                (rs, rowNum) -> rs.getString("name"), filmId);
    }

    @Override
    public boolean isFilmPresent(Integer id) {
        return getFilm(id).isPresent();
    }

    /**
     * Обновляет жанры фильма: сначала удаляет все текущие связи между фильмом и жанрами,
     * затем добавляет новые записи для каждого идентификатора из `genresId`.
     *
     * @param filmId   идентификатор фильма
     * @param genresId множество идентификаторов жанров (может быть пустым)
     */
    @Override
    public void updateFilmGenre(int filmId, Set<Integer> genresId) {
        jdbc.update(DELETE_GENRES_FOR_FILM, filmId);
        addGenresForFilm(filmId, genresId);
    }

    @Override
    public void addLike(int filmId, int userId) {
        int rowsUpdated = jdbc.update(INSERT_FILM_LIKE, userId, filmId);
    }

    @Override
    public void removeLike(int filmId, int userId) {
        int rowsDeleted = jdbc.update(DELETE_FILM_LIKE, userId, filmId);
    }
}