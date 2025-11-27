package ru.yandex.practicum.filmorate.storage.friendship;

public interface FriendshipStorage {

    void addFriend(int userId, int friendId);

    void removeFriend(int userId, int friendId);

    boolean areFriends(int userId, int friendId);
}
