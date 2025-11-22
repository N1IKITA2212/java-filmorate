package ru.yandex.practicum.filmorate.dto.film;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FilmDto {
    private Integer id;
    private String name;
    private String description;
    private Integer duration;
    private LocalDate releaseDate;
    private String mpa;
    // Set хранит жанры в строковом представлении
    private List<String> genres;
    // Set хранит email пользователей, поставивших лайк фильму
    private List<String> likes;
}
