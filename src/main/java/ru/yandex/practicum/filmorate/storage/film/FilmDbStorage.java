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

/**
 * Хранилище фильмов, работающее с реляционной базой данных через {@link JdbcTemplate}.
 * Здесь сосредоточена вся логика чтения и записи сущности {@link Film}.
 * <p>
 * Класс использует собственный {@link RowMapper} и {@link ResultSetExtractor},
 * поскольку фильм в базе хранится в нескольких таблицах
 * (основные данные фильма, жанры, лайки).
 * <p>
 * Методы возвращают полностью собранные объекты Film с жанрами, лайками и MPA-рейтингов.
 */
@Repository("filmDbStorage")
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    /**
     * SQL-запрос для загрузки всех фильмов вместе с жанрами и лайками.
     * Используется LEFT JOIN — фильм будет получен даже если у него нет жанров или лайков.
     */
    private static final String GET_ALL_FILMS_WITH_GENRES_AND_MPA = """
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
    /**
     * SQL-запрос для добавления нового фильма.
     */
    private static final String INSERT_FILM_QUERY = "INSERT INTO films (name, description, duration, release_date," +
            " rating_id) VALUES (?, ?, ?, ?, ?)";
    /**
     * SQL-запрос для обновления существующего фильма.
     */
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, duration = ?, " +
            "release_date = ?, rating_id = ? WHERE id = ?";
    /**
     * SQL-запрос получения фильма по ID с жанрами и лайками.
     */
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
    /**
     * SQL — получить имена пользователей, поставивших лайк фильму.
     */
    private static final String GET_USERS_NAME_LIKED_FILM = """
            SELECT u.name
            FROM users AS u
            JOIN film_likes AS fl ON u.id = fl.user_id
            WHERE fl.film_id = ?
            """;
    /**
     * SQL — получить названия жанров фильма.
     */
    private static final String GET_FILM_GENRE = """
            SELECT g.name
            FROM genres AS g
            JOIN film_genre AS fg ON g.id = fg.genre_id
            WHERE fg.film_id = ?
            """;
    /**
     * SQL — получить название рейтинга фильма.
     */
    private static final String GET_FILM_RATING = """
            SELECT r.name
            FROM ratings AS r
            JOIN films AS f ON r.id = f.rating_id
            WHERE f.id = ?
            """;

    /**
     * SQL — добавить лайк фильму.
     */
    private static final String INSERT_FILM_LIKE = """
            INSERT INTO film_likes (user_id, film_id)
            VALUES (?, ?)
            """;
    /**
     * SQL — удалить лайк фильма.
     */
    private static final String DELETE_FILM_LIKE = """
            DELETE FROM film_likes
            WHERE
            user_id = ? AND film_id = ?
            """;
    /**
     * SQL — добавить жанры фильму (batch-операция).
     */
    private static final String INSERT_GENRES_FOR_FILM = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
    /**
     * SQL — удалить все жанры, связанные с фильмом.
     */
    private static final String DELETE_GENRES_FOR_FILM = "DELETE from film_genre WHERE film_id = ?";

    private final JdbcTemplate jdbc;
    private final RowMapper<Film> mapper;
    private final ResultSetExtractor<List<Film>> filmExtractor;

    /**
     * Возвращает список всех фильмов с жанрами, лайками и рейтингами.
     * Используется один большой SQL-запрос + кастомный extractor,
     * который собирает все строки в полноценные Film-объекты.
     *
     * @return список всех фильмов в системе
     */
    @Override
    public List<Film> getAllFilms() {
        return jdbc.query(GET_ALL_FILMS_WITH_GENRES_AND_MPA, filmExtractor);
    }

    /**
     * Добавляет новый фильм в базу.
     * Используется {@link GeneratedKeyHolder} для получения ID, созданного базой.
     *
     * @param film объект фильма без ID
     * @return объект фильма с проставленным ID
     * @throws InternalServerException если ID не удалось получить
     */
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

    /**
     * Обновляет существующий фильм в базе.
     * После обновления метод повторно загружает фильм, чтобы вернуть актуальное состояние.
     *
     * @param film объект фильма с обновлёнными данными
     * @return обновлённый объект фильма, собранный заново из базы
     * @throws InternalServerException если фильм не найден или обновление не выполнено
     */
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

    /**
     * Получает фильм по его идентификатору.
     *
     * @param filmId ID фильма
     * @return Optional с фильмом, если найден; пустой Optional — если нет
     */
    @Override
    public Optional<Film> getFilm(int filmId) {
        List<Film> films = jdbc.query(GET_FILM_BY_ID_QUERY, filmExtractor, filmId);
        if (films == null || films.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(films.get(0));
        }
    }

    /**
     * Добавляет набор жанров фильму.
     * Используется batch-операция, что значительно быстрее одиночных insert-запросов.
     *
     * @param filmId   ID фильма
     * @param genresId множество ID жанров (может быть пустым)
     */
    public void addGenresForFilm(int filmId, Set<Integer> genresId) {
        jdbc.batchUpdate(INSERT_GENRES_FOR_FILM, genresId, genresId.size(),
                (ps, genreId) -> {
                    ps.setInt(1, filmId);
                    ps.setInt(2, genreId);
                });
    }

    /**
     * Возвращает список имён пользователей, которые поставили лайк фильму.
     *
     * @param filmId ID фильма
     * @return список имён пользователей
     */
    @Override
    public List<String> getUsersNamesLikedFilm(int filmId) {
        return jdbc.query(GET_USERS_NAME_LIKED_FILM,
                (rs, rowNum) -> rs.getString("name"), filmId);
    }

    /**
     * Получает список названий жанров фильма.
     *
     * @param filmId ID фильма
     * @return список жанров
     */
    @Override
    public List<String> getFilmGenre(int filmId) {
        return jdbc.query(GET_FILM_GENRE,
                (rs, rowNum) -> rs.getString("name"), filmId);
    }

    /**
     * Получает текстовое название рейтинга фильма.
     *
     * @param filmId ID фильма
     * @return строковое название рейтинга
     */
    @Override
    public String getFilmRating(int filmId) {
        return jdbc.queryForObject(GET_FILM_RATING,
                (rs, rowNum) -> rs.getString("name"), filmId);
    }

    /**
     * Проверяет существование фильма в базе.
     *
     * @param id ID фильма
     * @return true — фильм существует; false — фильм отсутствует
     */
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

    /**
     * Добавляет лайк фильму.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя, который ставит лайк
     */
    @Override
    public void addLike(int filmId, int userId) {
        int rowsUpdated = jdbc.update(INSERT_FILM_LIKE, userId, filmId);
    }

    /**
     * Удаляет лайк фильма.
     *
     * @param filmId ID фильма
     * @param userId ID пользователя, который удаляет лайк
     */
    @Override
    public void removeLike(int filmId, int userId) {
        int rowsDeleted = jdbc.update(DELETE_FILM_LIKE, userId, filmId);
    }
}