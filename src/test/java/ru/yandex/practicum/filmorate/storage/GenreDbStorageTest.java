package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreDbStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.config.location=classpath:application-test.properties")
class GenreDbStorageTest {

    @Autowired
    private GenreDbStorage genreDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testGetAllGenres() {
        List<Genre> genres = genreDbStorage.getGenres();
        assertEquals(6, genres.size());

        assertTrue(genres.stream().anyMatch(g -> g.getName().equals("Комедия")));
        assertTrue(genres.stream().anyMatch(g -> g.getName().equals("Драма")));
    }

    @Test
    void testGetGenreByIdExists() {
        Optional<Genre> genre = genreDbStorage.getGenreById(1);
        assertTrue(genre.isPresent());
        assertEquals("Комедия", genre.get().getName());
    }

    @Test
    void testGetGenreByIdNotExists() {
        Optional<Genre> genre = genreDbStorage.getGenreById(999);
        assertTrue(genre.isEmpty());
    }
}
