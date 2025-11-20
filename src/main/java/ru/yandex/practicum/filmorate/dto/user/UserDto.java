package ru.yandex.practicum.filmorate.dto.user;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UserDto {
    private String name;
    private String email;
    private String login;
    // List хранит email друзей пользователя
    private List<String> emailFriends;
    private LocalDate birthday;
    private Integer id;
}
