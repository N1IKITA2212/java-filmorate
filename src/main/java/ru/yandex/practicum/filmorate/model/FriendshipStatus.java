package ru.yandex.practicum.filmorate.model;

import lombok.Getter;

@Getter
public enum FriendshipStatus {
    CONFIRMED("Дружба подтверждена"),
    NOT_CONFIRMED("Дружба не подтверждена");

    private final String description;

    FriendshipStatus(String description) {
        this.description = description;
    }
}
