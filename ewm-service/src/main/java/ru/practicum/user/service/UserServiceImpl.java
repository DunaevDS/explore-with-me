package ru.practicum.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventMapper;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.model.SubscribersSort;
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
import ru.practicum.event.repository.EventRepository;
import ru.practicum.event.service.EventStatService;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserSubscriberRepository userSubscriberRepository;
    private final EventRepository eventRepository;
    private final EventStatService eventStatService;

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

        return UserMapper.toDtoWithSubscribers(user, subscribers);
    }

    @Override
    @Transactional
    public void deleteSubscriber(Long userId, Long subscriberId) {
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

    @Override
    public List<UserOutDto> getSubscribers(Long userId, List<Long> ids, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));

        List<UserSubscriber> users;
        Pageable pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            users = userSubscriberRepository.findAllByUserId(userId, pageRequest);
            log.info("users = " + users);
        } else {
            users = userSubscriberRepository.findAllByUserId(userId, pageRequest)
                    .stream()
                    .filter(userSubscriber -> ids.contains(userSubscriber.getUser().getId()))
                    .collect(Collectors.toList());
            log.info("users = " + users);
        }
        return UserMapper.toOutDtosSubs(users);
    }

    @Override
    public List<UserOutDto> getSubscriptions(Long userId, List<Long> ids, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id = " + userId + " не найден"));

        List<UserSubscriber> users;
        Pageable pageRequest = PageRequest.of(from / size, size);
        if (ids == null || ids.isEmpty()) {
            users = userSubscriberRepository.findAllBySubscriberId(userId, pageRequest);
            log.info("users = " + users);
        } else {
            users = userSubscriberRepository.findAllBySubscriberId(userId, pageRequest)
                    .stream()
                    .filter(userSubscriber -> ids.contains(userSubscriber.getUser().getId()))
                    .collect(Collectors.toList());
            log.info("users = " + users);
        }
        return UserMapper.toOutDtosSubscriptions(users);
    }

    @Override
    public List<EventFullDto> findEventsBySubscriptionOfUser(Long userId, Long subscriberId, Integer from, Integer size) {

        Pageable pageable = PageRequest.of(from / size, size);
        if (userId.equals(subscriberId)) {
            throw new DataConflictException("Пользователь не может быть подписан на себя");
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id" + userId + "не найден"));
        userRepository.findById(subscriberId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id" + subscriberId + "не найден"));

        UserSubscriber userSubscriber = userSubscriberRepository.findByUserIdAndSubscriberId(userId, subscriberId);
        if (userSubscriber == null) {
            throw new DataConflictException("Пользователь с id = " + subscriberId + " не подписан на" +
                    " пользователя с id = " + userId);
        }

        List<Event> events = eventRepository.findByInitiatorIdAndState(userId, EventState.PUBLISHED, pageable);

        Map<Long, Long> views = eventStatService.getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        log.info("Найдены события пользователя id {} для подписчика id {}", userId, subscriberId);

        return EventMapper.toFullDtos(events, views);
    }

    @Override
    public List<EventShortDto> findEventsByAllSubscriptions(Long subscriberId, String sort, Integer from, Integer size) {
        Pageable pageable;
        SubscribersSort subSort = SubscribersSort.valueOf(sort);
        if (subSort == SubscribersSort.NEW) {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate").descending());
        } else {
            pageable = PageRequest.of(from / size, size, Sort.by("eventDate"));
        }

        User subscriber = userRepository.findById(subscriberId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с id" + subscriberId + "не найден"));
        if (subscriber.getSubs().isEmpty()) {
            return new ArrayList<>();
        }
        List<Long> subs = subscriber.getSubs().stream().map(User::getId).collect(Collectors.toList());
        List<Event> events = eventRepository.findByStateAndInitiatorIdIn(EventState.PUBLISHED, subs, pageable);

        log.info("Найдены события по подпискам пользователя с id {}", subscriberId);
        return EventMapper.toShortDtos(events);
    }
}
