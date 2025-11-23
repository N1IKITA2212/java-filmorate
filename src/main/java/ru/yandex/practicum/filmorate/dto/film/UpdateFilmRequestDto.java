package ru.yandex.practicum.filmorate.dto.film;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO для запроса на обновление существующего фильма.
 * Содержит поля, необходимые для обновления фильма через PUT-запрос.
 */
@Data
public class UpdateFilmRequestDto {

    /**
     * Уникальный идентификатор фильма. Обязателен для обновления существующего фильма
     */
    private Integer id;

    /**
     * Название фильма. Не может быть пустым
     */
    @NotBlank(message = "Название не может быть пустым")
    private String name;

    /**
     * Описание фильма. Не может быть пустым, максимум 200 символов
     */
    @NotBlank(message = "Не указано описание")
    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;

    /**
     * Продолжительность фильма в минутах. Должна быть положительной
     */
    @NotNull(message = "Не указана продолжительность")
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;

    /**
     * Дата выхода фильма. Проверяется кастомной аннотацией @ValidReleaseDate
     */
    @NotNull(message = "Дата выхода не указана")
    @ValidReleaseDate
    private LocalDate releaseDate;

    /**
     * Рейтинг MPA фильма. Обязательное поле
     */
    @NotNull
    private MpaRequest mpa;

    /**
     * Список жанров фильма (опционально)
     */
    private List<GenreRequest> genres = new ArrayList<>();

    /**
     * DTO для передачи информации о рейтинге MPA.
     */
    @Data
    public static class MpaRequest {
        /**
         * Идентификатор рейтинга
         */
        private Integer id;
    }

    /**
     * DTO для передачи информации о жанре фильма.
     */
    @Data
    public static class GenreRequest {
        /**
         * Идентификатор жанра
         */
        private Integer id;
    }
}
