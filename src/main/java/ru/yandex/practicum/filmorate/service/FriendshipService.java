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
     * @param user_id   идентификатор пользователя
     * @param friend_id идентификатор друга
     * @throws IllegalArgumentException если user_id == friend_id
     * @throws NotFoundException        если один из пользователей не найден
     * @throws InternalServerException  если пользователи уже дружат
     */
    public void addFriend(int user_id, int friend_id) {
        if (user_id == friend_id) {
            throw new IllegalArgumentException("Нельзя добавлять в друзья самого себя");
        }
        if (!userStorage.isUserPresent(user_id) || !userStorage.isUserPresent(friend_id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        if (friendshipStorage.areFriends(user_id, friend_id)) {
            throw new InternalServerException("Пользователи уже дружат");
        }
        friendshipStorage.addFriend(user_id, friend_id);
    }

    /**
     * Удаляет друга пользователя.
     * Проверяет существование обоих пользователей.
     *
     * @param user_id   идентификатор пользователя
     * @param friend_id идентификатор друга
     * @throws NotFoundException если один из пользователей не найден
     */
    public void removeFriend(int user_id, int friend_id) {
        if (!userStorage.isUserPresent(user_id) || !userStorage.isUserPresent(friend_id)) {
            throw new NotFoundException("Пользователь не найден");
        }
        friendshipStorage.removeFriend(user_id, friend_id);
    }
}
