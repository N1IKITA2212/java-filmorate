package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Реализация интерфейса FriendshipStorage для работы с таблицей friendship в базе данных.
 * Поддерживает добавление, удаление и проверку дружбы между пользователями.
 */
@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendshipStorage {

    /**
     * SQL-запрос для добавления записи о дружбе
     */
    private static final String ADD_FRIEND =
            "INSERT into friendship (user_id, friend_id) VALUES (?, ?)";
    /**
     * SQL-запрос для удаления записи о дружбе
     */
    private static final String REMOVE_FRIEND =
            "DELETE from friendship WHERE user_id = ? AND friend_id = ?";
    /**
     * SQL-запрос для проверки существования дружбы между пользователями
     */
    private static final String SELECT_PAIR_COUNT =
            "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";
    private final JdbcTemplate jdbc;

    /**
     * Добавляет дружбу между двумя пользователями.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @Override
    public void addFriend(int userId, int friendId) {
        jdbc.update(ADD_FRIEND, userId, friendId);
    }

    /**
     * Удаляет дружбу между двумя пользователями.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     */
    @Override
    public void removeFriend(int userId, int friendId) {
        jdbc.update(REMOVE_FRIEND, userId, friendId);
    }

    /**
     * Проверяет, являются ли два пользователя друзьями.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     * @return true, если запись о дружбе существует; false — иначе
     */
    @Override
    public boolean areFriends(int userId, int friendId) {
        Integer count = jdbc.queryForObject(SELECT_PAIR_COUNT, Integer.class, userId, friendId);
        return count >= 1;
    }
}
