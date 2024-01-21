package ru.practicum.user.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.dto.UserWithSubscribersDto;
import ru.practicum.user.service.UserService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/subscribers")
public class UserControllerPrivate {

    private final UserService userService;

    @PostMapping(path = "/{subscriberId}")
    @ResponseStatus(HttpStatus.CREATED)
    public UserWithSubscribersDto addSubscriber(@PathVariable Long userId,
                                                @PathVariable Long subscriberId) {
        log.info(String.format("Получен запрос POST /users/{userId}/subscribers/{subscriberId} с параметрами " +
                "userId=%s, subscriberId=%s", userId, subscriberId));
        return userService.addSubscriber(userId, subscriberId);
    }

    @DeleteMapping(path = "/{subscriberId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSubscriber(@PathVariable Long userId,
                                 @PathVariable Long subscriberId) {
        log.info(String.format("Получен запрос DELETE /users/{userId}/subscribers/{subscriberId} с параметрами " +
                "userId=%s, subscriberId=%s", userId, subscriberId));
        userService.deleteSubscriber(userId, subscriberId);
    }

    @GetMapping
    public List<UserOutDto> getSubscribers(@PathVariable Long userId,
                                           @RequestParam(required = false) List<Long> ids,
                                      @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                      @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info(String.format("Получен запрос GET /users/{userId}/subscribers с параметрами " +
                "userId = %s, ids=%s, начиная с %s, по %s на странице", userId, ids, from, size));
        return userService.getSubscribers(userId, ids, from, size);
    }
}
