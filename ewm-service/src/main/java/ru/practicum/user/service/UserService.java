package ru.practicum.user.service;

import ru.practicum.user.dto.UserInDto;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    List<UserOutDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserOutDto addUser(UserInDto inDto);

    void deleteUser(Long userId);

    User findUserById(Long userId);
}
