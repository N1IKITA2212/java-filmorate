package ru.yandex.practicum.filmorate.storage.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class FilmExtractor implements ResultSetExtractor<List<Film>> {

    /**
     * Преобразует результирующий набор SQL-запроса в список объектов {@link Film}.
     * <p>
     * Источник данных обычно формируется SQL JOIN-ом:
     * один фильм может быть размножен на несколько строк — по количеству жанров и лайков.
     * Поэтому здесь используется LinkedHashMap, чтобы собрать все строки,
     * относящиеся к одному фильму, в единственный объект фильма.
     * <p>
     * Алгоритм работы:
     * - Идём по всем строкам ResultSet;
     * - Для каждого film_id либо создаём новый Film, либо достаём существующий из карты;
     * - Добавляем жанры и лайки к уже собранному объекту;
     * - В конце возвращаем список уникальных фильмов.
     *
     * @param rs ResultSet, содержащий строки SQL, где каждая строка может представлять один фильм
     *           или комбинацию фильм-жанр-лайк.
     * @return список уникальных фильмов с полностью собранными жанрами и лайками.
     * @throws SQLException            если JDBC обнаружит ошибку при работе с ResultSet.
     * @throws DataAccessException     если Spring обнаружит ошибку доступа к данным.
     * @throws InternalServerException если ошибка возникла при создании модели фильма.
     */
    @Override
    public List<Film> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            Film film = films.computeIfAbsent(id, k -> {
                Film f = new Film();
                f.setId(id);
                try {
                    f.setName(rs.getString("name"));
                    f.setDescription(rs.getString("description"));
                    f.setDuration(rs.getInt("duration"));
                    f.setReleaseDate(rs.getObject("release_date", LocalDate.class));
                    f.setMpa(Mpa.getMpaById(rs.getInt("rating_id")));
                } catch (SQLException e) {
                    throw new InternalServerException("Ошибка при получении фильма");
                }
                return f;
            });
            Integer genreId = rs.getObject("genre_id", Integer.class);
            Integer likeId = rs.getObject("like_id", Integer.class);
            if (genreId != null) {
                film.getGenres().add(Genre.getGenreById(genreId));
            }
            if (likeId != null) {
                film.getLikes().add(likeId);
            }
        }
        return new ArrayList<>(films.values());
    }
}
