package ru.practicum.service;

import ru.practicum.dto.StatisticInDto;
import ru.practicum.dto.StatisticViewDto;

import java.util.List;

public interface StatisticService {

    void postHit(StatisticInDto inDto);

    List<StatisticViewDto> getStatistic(String start, String end, List<String> uris, Boolean unique);
}
