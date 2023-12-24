package ru.practicum.user.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.user.dto.UserInDto;
import ru.practicum.user.dto.UserOutDto;
import ru.practicum.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
public class UserControllerAdmin {

    private final UserService userService;

    @GetMapping
    public List<UserOutDto> findUsers(@RequestParam(required = false) List<Long> ids,
                                      @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                      @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info(String.format("Получен запрос GET /admin/users на получение списка пользователей с id = %s, " +
                "начиная с %s, по %s на странице", ids, from, size));
        return userService.findUsers(ids, from, size);
    }

    @DeleteMapping(value = "/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@NotNull @PathVariable Long userId) {
        log.info(String.format("Получен запрос DELETE /admin/users/{userId} = %s на удаление пользователя", userId));
        userService.deleteUser(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserOutDto addUser(@Valid @RequestBody UserInDto inDto) {
        log.info("Получен запрос POST /admin/users на добавление нового пользователя " + inDto.toString());
        return userService.addUser(inDto);
    }
}
