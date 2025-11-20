package ru.yandex.practicum.filmorate.storage.friendship;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendshipStorage {
    private final JdbcTemplate jdbc;

    private static final String ADD_FRIEND =
            "INSERT into friendship (user_id, friend_id) VALUES (?, ?)";
    private static final String REMOVE_FRIEND =
            "DELETE from friendship WHERE user_id = ? AND friend_id = ?";
    private static final String SELECT_PAIR_COUNT =
            "SELECT COUNT(*) FROM friendship WHERE user_id = ? AND friend_id = ?";

    @Override
    public void addFriend(int user_id, int friend_id) {
        jdbc.update(ADD_FRIEND, user_id, friend_id);
    }

    @Override
    public void removeFriend(int user_id, int friend_id) {
        jdbc.update(REMOVE_FRIEND, user_id, friend_id);
    }

    @Override
    public boolean areFriends(int user_id, int friend_id) {
        Integer count = jdbc.queryForObject(SELECT_PAIR_COUNT, Integer.class, user_id, friend_id);
        return count >= 1;
    }
}
