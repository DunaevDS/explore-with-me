package ru.practicum.event.controller.priv;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import java.util.List;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/events")
public class EventControllerPrivate {

    private final EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto newEventDto) {
        log.info(String.format("Получен запрос POST /users/{userId}/ = %s/events на добавление события", userId));

        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping
    public List<EventShortDto> findEventsOfUser(@PathVariable Long userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info(String.format("Получен запрос GET /users/{userId}/events на получение списка событий " +
                "начатых пользователем с id = %s, с %s, по %s", userId, from, size));
        return eventService.findEventsOfUser(userId, from, size);
    }

    @GetMapping(path = "/{eventId}")
    public EventFullDto findUserEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info(String.format("Получен запрос GET /users/{userId} = %s/events/{eventId} = %s " +
                "на просмотр полной информации о событии", userId, eventId));
        return eventService.findUserEventById(userId, eventId);
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto userUpdateEvent(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @Valid @RequestBody UpdateEventUserRequest updateEventUserRequest) {
        log.info(String.format("Получен запрос PATCH /users/{userId} = %s/events/{eventId} = %s " +
                "на редактирование информации о событии", userId, eventId));
        return eventService.userUpdateEvent(userId, eventId, updateEventUserRequest);
    }

    @GetMapping(path = "/{eventId}/requests")
    public List<ParticipationRequestDto> findUserEventRequests(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info(String.format("Получен запрос GET /users/{userId} = %s/events/{eventId} = %s /requests " +
                "на получение списка заявок на участие в событии", userId, eventId));
        return eventService.findUserEventRequests(userId, eventId);
    }

    @PatchMapping(path = "/{eventId}/requests")
    public EventRequestStatusUpdateResult changeEventRequestsStatus(@PathVariable Long userId,
                                                                    @PathVariable Long eventId,
                                                                    @Valid @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        log.info(String.format("Получен запрос PATCH /users/{userId} = %s/events/{eventId} = %s /requests " +
                "на изменение статуса заявки", userId, eventId));
        return eventService.changeEventRequestsStatus(userId, eventId, updateRequest);
    }
}
