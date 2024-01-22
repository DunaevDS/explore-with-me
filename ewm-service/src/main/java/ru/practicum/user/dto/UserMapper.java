package ru.practicum.user.dto;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.user.model.User;
import ru.practicum.user.model.UserSubscriber;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class UserMapper {

    public static User toUserSubscriber(UserInDto inDto) {
        User user = new User();
        user.setEmail(inDto.getEmail());
        user.setId(user.getId());
        user.setName(inDto.getName());
        return user;
    }

    public static UserOutDto toUserOutDto(User user) {
        return UserOutDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static UserOutDto toUserOutDtoSubs(User user) {
        return UserOutDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public static List<UserOutDto> toOutDtos(List<User> users) {
        List<UserOutDto> dtos = new ArrayList<>();
        for (User user : users) {
            dtos.add(toUserOutDto(user));
        }
        return dtos;
    }

    public static List<UserOutDto> toOutDtosSubs(List<UserSubscriber> users) {
        List<UserOutDto> dtos = new ArrayList<>();
        for (UserSubscriber user : users) {
            log.info("user из UserSubscriber = " + user);
            dtos.add(toUserOutDtoSubs(user.getSubscriber()));
        }
        return dtos;
    }

    public static List<UserOutDto> toOutDtosSubscriptions(List<UserSubscriber> users) {
        List<UserOutDto> dtos = new ArrayList<>();
        for (UserSubscriber user : users) {
            log.info("user из UserSubscriber = " + user);
            dtos.add(toUserOutDtoSubs(user.getUser()));
        }
        return dtos;
    }

    public static UserShortDto toUserShortDto(User user) {
        return new UserShortDto(user.getId(), user.getName());
    }

    public static UserWithSubscribersDto toDtoWithSubscribers(User user, List<UserSubscriber> subscribers) {
        log.info("toDtoWithSubscribers method");
        log.info("user = " + user);
        log.info("subscribers = " + subscribers);

        return UserWithSubscribersDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .subscribers(toOutDtosSubs(subscribers))
                .build();
    }

    public static UserSubscriber toUserSubscriber(User eventOwner, User subscriber) {
        UserSubscriber user = new UserSubscriber();
        user.setUser(eventOwner);
        user.setSubscriber(subscriber);
        return user;
    }
}
