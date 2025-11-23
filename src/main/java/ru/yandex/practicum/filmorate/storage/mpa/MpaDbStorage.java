package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {

    private static final String SELECT_ALL_MPA = """
            SELECT
            id,
            name
            FROM ratings
            """;
    private static final String SELECT_MPA_BY_ID = """
            SELECT
            id,
            name
            FROM ratings
            WHERE id = ?
            """;

    private final JdbcTemplate jdbc;
    private final RowMapper<Mpa> mpaRowMapper;

    @Override
    public List<Mpa> getAllMpa() {
        return jdbc.query(SELECT_ALL_MPA, mpaRowMapper);
    }

    @Override
    public Mpa getMpaById(int id) {
        return jdbc.queryForObject(SELECT_MPA_BY_ID, mpaRowMapper, id);
    }
}
