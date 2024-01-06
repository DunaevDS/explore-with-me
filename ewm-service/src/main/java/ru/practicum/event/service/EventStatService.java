package ru.practicum.event.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import ru.practicum.StatisticClient;
import ru.practicum.constant.Constant;
import ru.practicum.dto.StatisticRequestDto;
import ru.practicum.dto.StatisticViewDto;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventStatService {

    private final StatisticClient statisticClient;
    private final ObjectMapper objectMapper;
    private final Gson gson;

    public Map<Long, Long> getEventsViews(List<Long> events) {
        List<StatisticViewDto> stats;
        Map<Long, Long> eventsViews = new HashMap<>();
        List<String> uris = new ArrayList<>();

        if (events == null || events.isEmpty()) {
            return eventsViews;
        }
        for (Long id : events) {
            uris.add(Constant.EVENT_URI + id);
            log.info("добавление в uris записи " + Constant.EVENT_URI + id);
        }

        StatisticRequestDto requestDto = new StatisticRequestDto();
        requestDto.setStart(LocalDateTime.now().minusDays(100).format(Constant.FORMATTER));
        requestDto.setEnd(LocalDateTime.now().format(Constant.FORMATTER));
        requestDto.setUris(uris);
        requestDto.setUnique(true);
        log.info("requestDto = " + requestDto);

        ResponseEntity<Object> response = statisticClient.getStatistics(requestDto);
        log.info("response = " + response);
        Object body = response.getBody();
        log.info("body = " + body);
        if (body != null) {
            String json = gson.toJson(body);
            log.info("json = " + json);
            TypeReference<List<StatisticViewDto>> typeRef = new TypeReference<>() {
            };
            try {
                stats = objectMapper.readValue(json, typeRef);
                log.info("stats = " + stats);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Ошибка при загрузке данных из сервиса статистики");
            }
            for (Long event : events) {
                eventsViews.put(event, eventsViews.getOrDefault(event, 0L) + 1);
                log.info("в eventsViews заносим event" + event);
                log.info("eventViews = " + eventsViews);
            }
            if (!stats.isEmpty()) {
                for (StatisticViewDto stat : stats) {
                    eventsViews.put(Long.parseLong(stat.getUri().split("/", 0)[2]),
                            stat.getHits());
                    log.info("в eventsViews заносим event" + stat);
                    log.info("eventViews = " + eventsViews);
                }
            }
        }
        log.info("на выходе из getEventsViews eventsViews = " + eventsViews);
        return eventsViews;
    }
}
