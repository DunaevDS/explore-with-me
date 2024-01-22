package ru.practicum.user.service;

import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.user.dto.UserInDto;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.dto.UserWithSubscribersDto;
import ru.practicum.user.model.User;

import java.util.List;

public interface UserService {

    List<UserOutDto> findUsers(List<Long> ids, Integer from, Integer size);

    UserOutDto addUser(UserInDto inDto);

    void deleteUser(Long userId);

    User findUserById(Long userId);

    UserWithSubscribersDto addSubscriber(Long userId, Long subscriberId);

    void deleteSubscriber(Long userId, Long subscriberId);

    List<UserOutDto> getSubscribers(Long userId, List<Long> ids, Integer from, Integer size);

    List<UserOutDto> getSubscriptions(Long userId, List<Long> ids, Integer from, Integer size);

    List<EventFullDto> findEventsBySubscriptionOfUser(Long userId, Long subscriberId, Integer from, Integer size);

    List<EventShortDto> findEventsByAllSubscriptions(Long subscriberId, String sort, Integer from, Integer size);
}
