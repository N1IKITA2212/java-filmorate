package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.config.location=classpath:application-test.properties")
class FilmDbStorageTest {

    @Autowired
    private FilmDbStorage filmDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("DELETE FROM film_genre");
        jdbcTemplate.update("DELETE FROM film_likes");
        jdbcTemplate.update("DELETE FROM films");
        jdbcTemplate.update("INSERT INTO users (email, login, name, birthday) VALUES ('test@mail.com','login1','User1','2000-01-01')");
    }

    @Test
    void testAddAndGetFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setDuration(120);
        film.setReleaseDate(LocalDate.of(2020, 1, 1));
        film.setMpa(Mpa.G);

        Film savedFilm = filmDbStorage.addFilm(film);
        assertNotNull(savedFilm.getId());

        Optional<Film> fetched = filmDbStorage.getFilm(savedFilm.getId());
        assertTrue(fetched.isPresent());
        assertEquals("Test Film", fetched.get().getName());
    }

    @Test
    void testUpdateFilm() {
        Film film = new Film();
        film.setName("Old Name");
        film.setDescription("Desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2021, 5, 5));
        film.setMpa(Mpa.G);

        Film savedFilm = filmDbStorage.addFilm(film);
        savedFilm.setName("New Name");
        savedFilm.setDuration(150);

        Film updatedFilm = filmDbStorage.updateFilm(savedFilm);
        assertEquals("New Name", updatedFilm.getName());
        assertEquals(150, updatedFilm.getDuration());
    }

    @Test
    void testAddGenresAndGetFilmGenre() {
        Film film = new Film();
        film.setName("Genre Film");
        film.setDescription("Desc");
        film.setDuration(90);
        film.setReleaseDate(LocalDate.of(2022, 2, 2));
        film.setMpa(Mpa.G);

        Film savedFilm = filmDbStorage.addFilm(film);
        filmDbStorage.addGenresForFilm(savedFilm.getId(), Set.of(1));

        List<String> genres = filmDbStorage.getFilmGenre(savedFilm.getId());
        assertEquals(1, genres.size());
        assertEquals("Comedy", genres.get(0));
    }

    @Test
    void testUpdateFilmGenre() {
        Film film = new Film();
        film.setName("Update Genre Film");
        film.setDescription("Desc");
        film.setDuration(110);
        film.setReleaseDate(LocalDate.of(2023, 3, 3));
        film.setMpa(Mpa.G);

        Film savedFilm = filmDbStorage.addFilm(film);

        // Добавляем жанр
        filmDbStorage.updateFilmGenre(savedFilm.getId(), Set.of(1));
        List<String> genres = filmDbStorage.getFilmGenre(savedFilm.getId());
        assertEquals(1, genres.size());

        // Обновляем на пустой набор
        filmDbStorage.updateFilmGenre(savedFilm.getId(), Set.of());
        genres = filmDbStorage.getFilmGenre(savedFilm.getId());
        assertTrue(genres.isEmpty());
    }

    @Test
    void testIsFilmPresent() {
        Film film = new Film();
        film.setName("Check Film");
        film.setDescription("Desc");
        film.setDuration(100);
        film.setReleaseDate(LocalDate.of(2021, 1, 1));
        film.setMpa(Mpa.G);

        Film savedFilm = filmDbStorage.addFilm(film);

        assertTrue(filmDbStorage.isFilmPresent(savedFilm.getId()));
        assertFalse(filmDbStorage.isFilmPresent(9999));
    }

    @Test
    void testGetAllFilms() {
        Film film1 = new Film();
        film1.setName("Film1");
        film1.setDescription("Desc1");
        film1.setDuration(100);
        film1.setReleaseDate(LocalDate.of(2021, 1, 1));
        film1.setMpa(Mpa.G);
        filmDbStorage.addFilm(film1);

        Film film2 = new Film();
        film2.setName("Film2");
        film2.setDescription("Desc2");
        film2.setDuration(120);
        film2.setReleaseDate(LocalDate.of(2022, 2, 2));
        film2.setMpa(Mpa.G);
        filmDbStorage.addFilm(film2);

        List<Film> films = filmDbStorage.getAllFilms();
        assertEquals(2, films.size());
    }
}
