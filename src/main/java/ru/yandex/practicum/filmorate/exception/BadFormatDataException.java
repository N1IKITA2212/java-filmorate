package ru.yandex.practicum.filmorate.exception;

public class BadFormatDataException extends RuntimeException {
    public BadFormatDataException(String message) {
        super(message);
    }
}
