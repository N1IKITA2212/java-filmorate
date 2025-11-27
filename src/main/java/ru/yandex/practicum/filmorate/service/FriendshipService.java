package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.friendship.FriendshipStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

/**
 * Сервис для управления дружбой между пользователями.
 * Обеспечивает добавление и удаление друзей с проверкой корректности данных.
 */
@Service
public class FriendshipService {

    private final FriendshipStorage friendshipStorage;
    private final UserStorage userStorage;

    public FriendshipService(FriendshipStorage friendshipStorage,
                             @Qualifier("userDbStorage") UserStorage userStorage) {
        this.friendshipStorage = friendshipStorage;
        this.userStorage = userStorage;
    }

    /**
     * Добавляет друга пользователю.
     * Проверяет:
     * - что пользователь не добавляет самого себя,
     * - что оба пользователя существуют,
     * - что пользователи еще не являются друзьями.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     * @throws IllegalArgumentException если userId == friendId
     * @throws NotFoundException        если один из пользователей не найден
     * @throws InternalServerException  если пользователи уже дружат
     */
    public void addFriend(int userId, int friendId) {
        if (userId == friendId) {
            throw new IllegalArgumentException("Нельзя добавлять в друзья самого себя");
        }
        if (!userStorage.isUserPresent(userId) || !userStorage.isUserPresent(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (friendshipStorage.areFriends(userId, friendId)) {
            throw new InternalServerException("Пользователи уже дружат");
        }
        friendshipStorage.addFriend(userId, friendId);
    }

    /**
     * Удаляет друга пользователя.
     * Проверяет существование обоих пользователей.
     *
     * @param userId   идентификатор пользователя
     * @param friendId идентификатор друга
     * @throws NotFoundException если один из пользователей не найден
     */
    public void removeFriend(int userId, int friendId) {
        if (!userStorage.isUserPresent(userId) || !userStorage.isUserPresent(friendId)) {
            throw new NotFoundException("Пользователь не найден");
        }
        friendshipStorage.removeFriend(userId, friendId);
    }
}
