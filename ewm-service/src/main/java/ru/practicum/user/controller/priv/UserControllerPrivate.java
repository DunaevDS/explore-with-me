package ru.practicum.user.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserWithSubscribersDto;
import ru.practicum.user.service.UserService;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/subscribers/{subscriberId}")
public class UserControllerPrivate {

    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserWithSubscribersDto addSubscriber(@PathVariable Long userId,
                                              @PathVariable Long subscriberId) {
        log.info(String.format("Получен запрос POST /users/{userId}/subscribers/{subscriberId} с параметрами " +
                "userId=%s, subscriberId=%s", userId, subscriberId));
        return userService.addSubscriber(userId, subscriberId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscriber(@PathVariable Long userId,
                                 @PathVariable Long subscriberId) {
        log.info(String.format("Получен запрос DELETE /users/{userId}/subscribers/{subscriberId} с параметрами " +
                "userId=%s, subscriberId=%s", userId, subscriberId));
        userService.deleteSubscriber(userId, subscriberId);
    }
}
