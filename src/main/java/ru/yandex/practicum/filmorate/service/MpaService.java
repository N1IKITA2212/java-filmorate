package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

/**
 * Сервис для работы с рейтингами фильмов (MPA).
 * Предоставляет методы для получения всех рейтингов и получения рейтинга по идентификатору.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MpaService {

    private final MpaStorage mpaStorage;

    /**
     * Получить список всех рейтингов фильмов.
     *
     * @return список объектов Mpa
     */
    public List<Mpa> getAllMpa() {
        return mpaStorage.getAllMpa();
    }

    /**
     * Получить рейтинг по его идентификатору.
     *
     * @param id идентификатор рейтинга
     * @return объект Mpa
     * @throws NotFoundException если рейтинг с указанным id не найден
     */
    public Mpa getMpaById(int id) {
        return mpaStorage.getMpaById(id)
                .orElseThrow(() -> new NotFoundException("Рейтинг с id " + id + " не существует"));
    }
}
