package ru.yandex.practicum.filmorate.model;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

public class UserTest {
    private Validator validator;

    @BeforeEach
    void setUp() {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    @Test
    public void validUserValidationTest() {
        User user = new User();
        user.setName("имя");
        user.setBirthday(LocalDate.of(2000, 12, 12));
        user.setEmail("123@ya.ru");
        user.setLogin("name");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertTrue(violations.isEmpty());
    }

    @Test
    public void badEmailValidationTest() {
        User user = new User();
        user.setName("имя");
        user.setBirthday(LocalDate.of(2000, 12, 12));
        user.setEmail("@123.ru");
        user.setLogin("name");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("email")));
    }

    @Test
    public void emailNullValidationTest() {
        User user = new User();
        user.setName("имя");
        user.setBirthday(LocalDate.of(2000, 12, 12));
        user.setLogin("name");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("email")));
    }

    @Test
    public void loginWithSpacesValidationTest() {
        User user = new User();
        user.setName("имя");
        user.setBirthday(LocalDate.of(2000, 12, 12));
        user.setEmail("123@ya.ru");
        user.setLogin("na me");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("login")));
    }

    @Test
    public void loginNullValidationTest() {
        User user = new User();
        user.setName("имя");
        user.setBirthday(LocalDate.of(2000, 12, 12));
        user.setEmail("123@ya.ru");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("login")));
    }

    @Test
    public void birthdayInFutureValidationTest() {
        User user = new User();
        user.setName("имя");
        user.setBirthday(LocalDate.of(2030, 12, 12));
        user.setEmail("123@ya.ru");
        user.setLogin("name");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("birthday")));
    }

    @Test
    public void birthdayNullValidationTest() {
        User user = new User();
        user.setName("имя");
        user.setEmail("123@ya.ru");
        user.setLogin("name");

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        Assertions.assertEquals(1, violations.size());
        Assertions.assertTrue(violations.stream().anyMatch(v -> v.getPropertyPath().toString()
                .equals("birthday")));
    }
}
