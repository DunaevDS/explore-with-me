package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.exception.DataConflictException;
import ru.practicum.exception.DataValidationException;
import ru.practicum.exception.UserNotFoundException;
import ru.practicum.user.dto.UserInDto;
import ru.practicum.user.dto.UserMapper;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.dto.UserWithSubscribersDto;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserSubscriber;
import ru.practicum.user.repository.UserRepository;
import ru.practicum.user.repository.UserSubscriberRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSubscriberRepository userSubscriberRepository;

    @Override
    public List<UserOutDto> findUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        Pageable pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAll(pageRequest).getContent();
        } else {
            users = userRepository.findByIdIn(ids, pageRequest);
        }
        log.info("Выполняется запрос на поиск пользователей. Выбранные id: {}", ids);
        return UserMapper.toOutDtos(users);
    }

    @Override
    @Transactional
    public UserOutDto addUser(UserInDto inDto) {
        User user = UserMapper.toUserSubscriber(inDto);
        log.info("Добавление нового пользователя");
        return UserMapper.toUserOutDto(userRepository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new DataValidationException("Пользователя с заданным id не существует");
        }
        userRepository.deleteById(userId);
        log.info("Пользователь с id {} удалён", userId);
    }

    @Override
    public User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id" + userId + "не найден"));
    }

    @Override
    @Transactional
    public UserWithSubscribersDto addSubscriber(Long userId, Long subscriberId) {
        log.info("метод addSubscriber");

        if (userId.equals(subscriberId)) {
            throw new DataConflictException("Пользователь не может подписаться на себя");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));
        log.info("user = " + user);
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + subscriberId + " не найден"));
        log.info("subscriber = " + subscriber);

        UserSubscriber existingSubscriber = userSubscriberRepository.findByUserIdAndSubscriberId(userId, subscriberId);
        if (existingSubscriber != null) {
            throw new DataConflictException("Пользователь с id = " + subscriberId + " уже подписан на пользователя с id " + userId);
        }

        UserSubscriber userSubscriber = UserMapper.toUserSubscriber(user, subscriber);
        log.info("userSubscriber model = " + userSubscriber);

        userSubscriberRepository.save(userSubscriber);
        log.info("Пользователь с id = {} подписался на пользователя с id = {}", subscriberId, userId);

        List<UserSubscriber> subscribers = userSubscriberRepository.findByUserId(userId);
        log.info("subscribers = " + subscribers);
        UserWithSubscribersDto dto = UserMapper.toDtoWithSubscribers(user, subscribers);
        log.info("dto на выходе из метода = " + dto);
        return dto;
    }

    @Override
    @Transactional
    public void deleteSubscriber(Long userId, Long subscriberId) {
        log.info("метод deleteSubscriber");

        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + "не найден"));
        userRepository.findById(subscriberId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + subscriberId + "не найден"));

        UserSubscriber userSubscriber = userSubscriberRepository.findByUserIdAndSubscriberId(userId, subscriberId);
        if (userSubscriber == null) {
            throw new DataConflictException("Пользователь с id = " + subscriberId + " не подписан на" +
                    " пользователя с id = " + userId);
        }

        userSubscriberRepository.delete(userSubscriber);

        log.info("Пользователь с id = {} отписал от пользователя с id = {}", subscriberId, userId);
    }
}
