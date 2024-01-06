package ru.practicum.request.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.NotNull;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/")
public class RequestControllerPrivate {

    private final RequestService requestService;

    @GetMapping(path = "/{userId}/requests")
    public List<ParticipationRequestDto> findUserRequests(@PathVariable Long userId) {
        log.info(String.format("Получен запрос GET /users/{userId} = %s /requests", userId));
        return requestService.findUserRequests(userId);
    }

    @PostMapping(path = "/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId,
                                              @NotNull @RequestParam Long eventId) {
        log.info(String.format(
                "Получен запрос POST /users/{userId} = %s /requests на добавления заявки участия в событии с id = %s",
                userId, eventId));
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping(path = "/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto rejectRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info(String.format(
                "Получен запрос PATCH /users/{userId}= %s/{requestId} = %s/cancel на отмену заявки участия в событии",
                userId, requestId));
        return requestService.rejectRequest(userId, requestId);
    }
}
