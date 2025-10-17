package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.yandex.practicum.filmorate.validation.ValidReleaseDate;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = {"id"})
public class Film {
    private Integer id;
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
}
