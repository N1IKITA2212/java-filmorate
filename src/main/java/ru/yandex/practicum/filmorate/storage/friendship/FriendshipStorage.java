package ru.yandex.practicum.filmorate.storage.friendship;

public interface FriendshipStorage {

    void addFriend(int user_id, int friend_id);

    void removeFriend(int user_id, int friend_id);

    boolean areFriends(int user_id, int friend_id);
}
