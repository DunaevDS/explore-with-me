package ru.practicum.event.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.constant.Constant.TIME_FORMAT;

@Validated
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/events")
public class EventControllerAdmin {

    private final EventService eventService;

    @GetMapping
    public List<EventFullDto> findEventsByAdmin(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeStart,
                                                @RequestParam(required = false) @DateTimeFormat(pattern = TIME_FORMAT) LocalDateTime rangeEnd,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        EventAdminParam eventAdminParam = new EventAdminParam(users, states, categories, rangeStart, rangeEnd, from, size);

        log.info(String.format("Получен запрос GET /admin/events с параметрами users=%s, states=%s, categories=%s, " +
                        "rangeStart=%s, rangeEnd=%s, from=%s, size=%s",
                users, states, categories, rangeStart, rangeEnd, from, size));

        return eventService.findEventsByAdmin(eventAdminParam);
    }

    @PatchMapping(path = "/{eventId}")
    public EventFullDto adminUpdateEvent(@PathVariable Long eventId,
                                         @Valid @RequestBody UpdateEventAdminRequest updateRequest) {

        log.info(String.format("Получен запрос PATCH /admin/events/{eventId} = %s", eventId));
        return eventService.adminUpdateEvent(eventId, updateRequest);
    }
}
