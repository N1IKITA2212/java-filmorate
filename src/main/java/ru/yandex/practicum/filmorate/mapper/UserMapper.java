package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public class UserMapper {

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
