package ru.yandex.practicum.filmorate.storage.extractors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserWithFriendsExtractor implements ResultSetExtractor<List<User>> {

    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, User> users = new LinkedHashMap<>();

        while (rs.next()) {
            int id = rs.getInt("id");
            User user = users.computeIfAbsent(id, k -> {
                User u = new User();
                u.setId(id);
                try {
                    u.setEmail(rs.getString("email"));
                    u.setLogin(rs.getString("login"));
                    u.setName(rs.getString("name"));
                    u.setBirthday(rs.getObject("birthday", LocalDate.class));
                } catch (SQLException e) {
                    throw new InternalServerException("Внутрення ошибка при извлечении данных");
                }
                return u;
            });
            Integer friendId = (Integer) rs.getObject("friend_id");
            if (friendId != null) {
                user.getFriends().add(friendId);
            }
        }
        return new ArrayList<>(users.values());
    }
}
