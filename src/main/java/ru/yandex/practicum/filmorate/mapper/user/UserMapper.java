package ru.yandex.practicum.filmorate.mapper.user;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Component
public class UserMapper {
    private final UserStorage userStorage;

    public UserMapper(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public UserDto toDto(User user, List<String> friendsEmails) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            userDto.setName(user.getLogin());
        } else {
            userDto.setName(user.getName());
        }
        userDto.setEmail(user.getEmail());
        userDto.setLogin(user.getLogin());
        userDto.setBirthday(user.getBirthday());
        userDto.setEmailFriends(friendsEmails);
        return userDto;
    }
}
