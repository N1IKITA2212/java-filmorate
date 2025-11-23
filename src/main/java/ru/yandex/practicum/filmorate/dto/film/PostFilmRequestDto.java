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

@Data
public class PostFilmRequestDto {
    @NotBlank(message = "Название не может быть пустым")
    private String name;
    @NotBlank(message = "Не указано описание")
    @Size(max = 200, message = "Длина описания не должна превышать 200 символов")
    private String description;
    @NotNull(message = "Не указана продолжительность")
    @Positive(message = "Продолжительность должна быть положительным числом")
    private Integer duration;
    @NotNull(message = "Дата выхода не указана")
    @ValidReleaseDate// В минутах
    private LocalDate releaseDate;
    @NotNull
    private MpaRequest mpa;
    private List<GenreRequest> genres = new ArrayList<>();

    @Data
    public static class MpaRequest {
        private Integer id;
    }

    @Data
    public static class GenreRequest {
        Integer id;
    }
}
