package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaDbStorage;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(properties = "spring.config.location=classpath:application-test.properties")
class MpaDbStorageTest {

    @Autowired
    private MpaDbStorage mpaDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void testGetAllMpa() {
        List<Mpa> ratings = mpaDbStorage.getAllMpa();
        assertEquals(5, ratings.size());

        assertTrue(ratings.stream().anyMatch(r -> r.getName().equals("G")));
        assertTrue(ratings.stream().anyMatch(r -> r.getName().equals("PG")));
    }

    @Test
    void testGetMpaByIdExists() {
        Optional<Mpa> rating = mpaDbStorage.getMpaById(1);
        assertTrue(rating.isPresent());
        assertEquals("G", rating.get().getName());
    }

    @Test
    void testGetMpaByIdNotExists() {
        Optional<Mpa> rating = mpaDbStorage.getMpaById(999);
        assertTrue(rating.isEmpty());
    }
}
