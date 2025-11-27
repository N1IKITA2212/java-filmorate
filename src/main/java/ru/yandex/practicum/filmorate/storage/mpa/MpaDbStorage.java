package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

/**
 * Реализация интерфейса MpaStorage для работы с таблицей рейтингов фильмов (ratings) в базе данных.
 * Поддерживает получение всех рейтингов и получение рейтинга по идентификатору.
 */
@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    /**
     * SQL-запрос для получения всех рейтингов
     */
    private static final String SELECT_ALL_MPA = """
            SELECT
            id,
            name
            FROM ratings
            """;

    /**
     * SQL-запрос для получения рейтинга по id
     */
    private static final String SELECT_MPA_BY_ID = """
            SELECT
            id,
            name
            FROM ratings
            WHERE id = ?
            """;

    private final JdbcTemplate jdbc;
    private final RowMapper<Mpa> mpaRowMapper;

    /**
     * Получает список всех рейтингов.
     *
     * @return список объектов Mpa
     */
    @Override
    public List<Mpa> getAllMpa() {
        return jdbc.query(SELECT_ALL_MPA, mpaRowMapper);
    }

    /**
     * Получает рейтинг по идентификатору.
     *
     * @param id идентификатор рейтинга
     * @return Optional с объектом Mpa, если найден; иначе пустой Optional
     */
    @Override
    public Optional<Mpa> getMpaById(int id) {
        try {
            return Optional.ofNullable(jdbc.queryForObject(SELECT_MPA_BY_ID, mpaRowMapper, id));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
