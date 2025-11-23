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

/**
 * ResultSetExtractor для извлечения списка пользователей из ResultSet.
 * Обрабатывает данные таблицы пользователей вместе с их друзьями.
 */
@Component
public class UserExtractor implements ResultSetExtractor<List<User>> {

    /**
     * Извлекает данные из ResultSet и формирует список пользователей.
     * Каждому пользователю добавляются его друзья по идентификаторам.
     *
     * @param rs ResultSet с результатами SQL-запроса
     * @return список объектов User с заполненными полями и множеством друзей
     * @throws SQLException        при ошибках доступа к данным
     * @throws DataAccessException при ошибках Spring JDBC
     */
    @Override
    public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, User> users = new LinkedHashMap<>();

        while (rs.next()) {
            int id = rs.getInt("id");

            // Создает нового пользователя, если еще не существует
            User user = users.computeIfAbsent(id, k -> {
                User u = new User();
                u.setId(id);
                try {
                    u.setEmail(rs.getString("email"));
                    u.setLogin(rs.getString("login"));
                    u.setName(rs.getString("name"));
                    u.setBirthday(rs.getObject("birthday", LocalDate.class));
                } catch (SQLException e) {
                    throw new InternalServerException("Внутренняя ошибка при извлечении данных");
                }
                return u;
            });

            // Добавляет идентификатор друга, если он присутствует
            Integer friendId = (Integer) rs.getObject("friend_id");
            if (friendId != null) {
                user.getFriends().add(friendId);
            }
        }

        return new ArrayList<>(users.values());
    }
}
