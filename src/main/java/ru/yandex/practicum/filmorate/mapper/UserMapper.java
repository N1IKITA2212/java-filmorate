package ru.yandex.practicum.filmorate.mapper;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dto.user.UserDto;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

/**
 * Mapper для преобразования между сущностью User и DTO UserDto.
 * Обеспечивает конвертацию User в DTO с учетом списка друзей.
 */
@Component
public class UserMapper {

    /**
     * Преобразует объект User в UserDto.
     * Если имя пользователя пустое или состоит только из пробелов,
     * в качестве имени используется логин.
     *
     * @param user          объект модели пользователя
     * @param friendsEmails список email друзей пользователя
     * @return объект UserDto с заполненными полями
     */
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
