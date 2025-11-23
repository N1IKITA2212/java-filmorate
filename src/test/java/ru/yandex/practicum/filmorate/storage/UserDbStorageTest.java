package ru.yandex.practicum.filmorate.storage;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(properties = "spring.config.location=classpath:application-test.properties")
class UserDbStorageTest {

    @Autowired
    private UserDbStorage userDbStorage;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private User user;

    @BeforeEach
    void setUp() {

        jdbcTemplate.update("DELETE from friendship");
        jdbcTemplate.update("DELETE FROM users");

        user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testuser");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(2000, 1, 1));
    }

    @Test
    void addUser_andGetUserById() {
        User saved = userDbStorage.addUser(user);
        assertNotNull(saved.getId());

        Optional<User> fetched = userDbStorage.getUser(saved.getId());
        assertTrue(fetched.isPresent());
        assertEquals(saved.getEmail(), fetched.get().getEmail());
    }

    @Test
    void getAllUsers_returnsAddedUsers() {
        userDbStorage.addUser(user);

        List<User> users = userDbStorage.getAllUsers();
        assertEquals(1, users.size());
        assertEquals(user.getEmail(), users.get(0).getEmail());
    }

    @Test
    void isUserPresent_returnsTrueForExistingUser() {
        User saved = userDbStorage.addUser(user);
        assertTrue(userDbStorage.isUserPresent(saved.getId()));
    }

    @Test
    void updateUser_updatesFieldsCorrectly() {
        User saved = userDbStorage.addUser(user);
        saved.setName("Updated Name");
        saved.setEmail("updated@example.com");

        User updated = userDbStorage.updateUser(saved);
        assertEquals("Updated Name", updated.getName());
        assertEquals("updated@example.com", updated.getEmail());
    }

    @Test
    void getFriendsEmails_returnsCorrectEmails() {
        User user2 = new User();
        user2.setEmail("friend@example.com");
        user2.setLogin("frienduser");
        user2.setName("Friend User");
        user2.setBirthday(LocalDate.of(2001, 2, 2));

        User savedUser = userDbStorage.addUser(user);
        User savedFriend = userDbStorage.addUser(user2);

        jdbcTemplate.update("INSERT INTO friendship (user_id, friend_id) VALUES (?, ?)",
                savedUser.getId(), savedFriend.getId());

        List<String> friendsEmails = userDbStorage.getFriendsEmails(savedUser.getId());
        assertEquals(1, friendsEmails.size());
        assertEquals(savedFriend.getEmail(), friendsEmails.get(0));
    }
}
