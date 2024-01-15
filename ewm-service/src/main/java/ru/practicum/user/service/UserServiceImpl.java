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
import ru.practicum.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

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
        User user = UserMapper.toUser(inDto);
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
        log.info("Зашли в метод addSubscriber");
        if (userId.equals(subscriberId)) {
            throw new DataConflictException("Пользователь не может подписаться на себя");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id" + userId + "не найден"));
        log.info("user in method = " + user);
        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id" + subscriberId + "не найден"));
        log.info("subscriber in method = " + subscriber);

        if (user.getSubscribers().contains(subscriber)) {
            throw new DataConflictException("Пользователь с id " + subscriberId + " уже подписан на пользователя с id "
                    + userId);
        }

        log.info("subscribers list перед добавлением нового подписчика в лист" + user.getSubscribers());
        user.getSubscribers().add(subscriber);
        log.info("subscribers = " + user.getSubscribers());

        user = userRepository.save(user);
        log.info("Пользователь с id {} подписался на пользователя с id {}", subscriberId, userId);

        log.info("dto на выходе = " + UserMapper.toDtoWithSubscribers(user));
        return UserMapper.toDtoWithSubscribers(user);
    }

    @Override
    @Transactional
    public void deleteSubscriber(Long userId, Long subscriberId) {
        if (userId.equals(subscriberId)) {
            throw new DataConflictException("Пользователь не может быть подписан на себя");
        }
        User user = findUserById(userId);
        User subscriber = findUserById(subscriberId);

        if (!user.getSubscribers().contains(subscriber)) {
            throw new DataConflictException("Пользователь с id " + subscriberId + " не подписан на пользователя с id "
                    + userId);
        }
        user.getSubscribers().remove(subscriber);
        log.info("Пользователь с id {} отписан от пользователя с id {}", subscriberId, userId);
        userRepository.save(user);
    }
}
