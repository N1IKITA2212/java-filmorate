package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

public class FilmTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validFilmValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("Мультик пиксар");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void filmNameNullValidationTest() {
        Film film = new Film();
        film.setDescription("Мультик пиксар");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("name")));

    }

    @Test
    public void filmDescriptionNullValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("description")));
    }

    @Test
    public void filmDescriptionLength200ValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("-".repeat(200));
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void filmDescriptionLength201ValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("-".repeat(201));
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("description")));
    }

    @Test
    public void filmDurationNullValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("Мультик пиксар");
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("duration")));
    }

    @Test
    public void filmDurationNegativeValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("Мультик пиксар");
        film.setDuration(-1);
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("duration")));
    }

    @Test
    public void filmDurationZeroValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("Мультик пиксар");
        film.setDuration(0);
        film.setReleaseDate(LocalDate.of(2005, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("duration")));
    }

    @Test
    public void filmReleaseDateNullValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("Мультик пиксар");
        film.setDuration(90);

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("releaseDate")));
    }

    @Test
    public void filmReleaseDateBefore1895ValidationTest() {
        Film film = new Film();
        film.setName("Тачки");
        film.setDescription("Мультик пиксар");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(1890, 1, 1));

        Set<ConstraintViolation<Film>> violations = validator.validate(film);

        Assertions.assertFalse(violations.isEmpty());
        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("releaseDate")));
    }
}
