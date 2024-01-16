package ru.practicum.event.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.StatisticClient;
import ru.practicum.dto.StatisticInDto;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constant.Constant.SERVICE_ID;
import static ru.practicum.constant.Constant.TIME_FORMAT;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
public class EventControllerPublic {

    private final EventService eventService;
    private final StatisticClient statisticClient;

    @GetMapping(path = "/events")
    public List<EventShortDto> findEventsByPublic(@RequestParam(required = false) String text,
                                                  @RequestParam(required = false) List<Long> categories,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @RequestParam(required = false, defaultValue = "0") Integer from,
                                                  @RequestParam(required = false, defaultValue = "10") Integer size,
                                                  HttpServletRequest request) {
        log.info(String.format("Получен GET /events запрос на получение списка событий с параметрами: text = %s, " +
                        "categories = %s, paid = %s, rangeStart = %s, rangeEnd = %s, onlyAvailable = %s, sort = %s, " +
                        "from = %s, size = %s", text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort,
                from, size));

        EventUserParam eventUserParam = new EventUserParam(text, categories, paid, rangeStart, rangeEnd, onlyAvailable,
                sort, from, size);
        StatisticInDto statisticInDto = new StatisticInDto(SERVICE_ID, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        statisticClient.postHit(statisticInDto);
        return eventService.findEventsByPublic(eventUserParam, request);
    }

    @GetMapping(path = "/events/{id}")
    public EventFullDto findPublishedEventById(@PathVariable Long id,
                                               HttpServletRequest request) {
        log.info(String.format("Получен запрос GET /events/{id} = %s на получение категории", id));
        StatisticInDto statisticInDto = new StatisticInDto(SERVICE_ID, request.getRequestURI(), request.getRemoteAddr(),
                LocalDateTime.now());
        statisticClient.postHit(statisticInDto);
        return eventService.findPublishedEventById(id, request);
    }

    @GetMapping(path = "/users/{userId}/subscribers/{subscriberId}/events")
    public List<EventFullDto> findEventsBySubscriptionOfUser(@PathVariable Long userId,
                                                             @PathVariable Long subscriberId,
                                                             @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                             @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info(String.format("Получен запрос GET /users/{userId}/subscribers/{subscriberId}/events c параметрами " +
                        "userId=%s, subscriberId=%s, from=%s, size=%s", userId, subscriberId, from, size));
        return eventService.findEventsBySubscriptionOfUser(userId, subscriberId, from, size);
    }

    @GetMapping(path = "/users/subscribers/{subscriberId}/events")
    public List<EventShortDto> findEventsByAllSubscriptions(@PathVariable Long subscriberId,
                                                            @RequestParam(required = false, defaultValue = "NEW") String sort,
                                                            @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                            @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        log.info(String.format("Получен запрос GET /users/subscribers/{subscriberId}/events c параметрами " +
                "subscriberId=%s, sort=%s, from=%s, size=%s", subscriberId, sort, from, size));
        return eventService.findEventsByAllSubscriptions(subscriberId, sort, from, size);
    }
}
