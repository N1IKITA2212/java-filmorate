package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class NotEnoughDataException extends RuntimeException {
    private final String parameter;

    public NotEnoughDataException(String message, String parameter) {
        super(message);
        this.parameter = parameter;
    }
}
