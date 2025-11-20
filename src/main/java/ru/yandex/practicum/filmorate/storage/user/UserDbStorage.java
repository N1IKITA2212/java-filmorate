package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository("userDbStorage")
public class UserDbStorage implements UserStorage {
    private static final String GET_ALL_USERS_WITH_FRIENDS = """
            SELECT u.id,
            u.email,
            u.login,
            u.name,
            u.birthday,
            f.friend_id
            FROM users AS u
            LEFT JOIN friendship AS f ON u.id = f.user_id
            """;

    private static final String GET_USER_WITH_FRIENDS = """
            SELECT
            u.id,
            u.email,
            u.login,
            u.name,
            u.birthday,
            f.friend_id
            FROM users AS u
            LEFT JOIN friendship AS f on u.id = f.user_id
            WHERE u.id = ?
            """;
    private static final String INSERT_USER_QUERY = "INSERT INTO users (email, login, name, birthday) " +
            "VALUES(?, ?, ?, ?)";
    private static final String UPDATE_USER_QUERY = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
            "WHERE id = ?";
    private static final String GET_FRIENDS_EMAILS = """
            SELECT
            fu.email
            FROM users AS u
            JOIN friendship AS f ON u.id = f.user_id
            JOIN users AS fu ON f.friend_id = fu.id
            WHERE u.id = ?;
            """;

    private final JdbcTemplate jdbc;
    private final RowMapper<User> userRowMapper;
    private final RowMapper<Integer> friendIdRowMapper;
    private final ResultSetExtractor<List<User>> userResultSetExtractor;
    private final RowMapper<String> emailRowMapper;

    @Override
    public List<User> getAllUsers() {
        return jdbc.query(GET_ALL_USERS_WITH_FRIENDS, userResultSetExtractor);
    }

    @Override
    public User addUser(User user) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(INSERT_USER_QUERY, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getEmail());
            ps.setString(2, user.getLogin());
            ps.setString(3, user.getName());
            ps.setObject(4, user.getBirthday());
            return ps;
        }, keyHolder);

        Integer id = keyHolder.getKeyAs(Integer.class);
        if (id != null) {
            user.setId(id);
            return user;
        } else {
            throw new InternalServerException("Не удалось сохранить пользователя");
        }
    }

    @Override
    public Optional<User> getUser(int userId) {
        List<User> list = jdbc.query(GET_USER_WITH_FRIENDS, userResultSetExtractor, userId);
        if (list == null || list.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(list.get(0));
        }
    }

    @Override
    public boolean isUserPresent(Integer id) {
        return getUser(id).isPresent();
    }

    @Override
    public User updateUser(User user) {
        Object[] params = {
                user.getEmail(), user.getLogin(), user.getName(), user.getBirthday(), user.getId()
        };
        int rowsUpdated = jdbc.update(UPDATE_USER_QUERY, params);
        if (rowsUpdated == 0) {
            throw new InternalServerException("Не удалось обновить пользователя");
        }
        return getUser(user.getId()).orElseThrow(() ->
                new InternalServerException("Пользователь не найден после обновления"));
    }

    @Override
    public List<String> getFriendsEmails(int id) {
        return jdbc.query(GET_FRIENDS_EMAILS, emailRowMapper, id);
    }
}
