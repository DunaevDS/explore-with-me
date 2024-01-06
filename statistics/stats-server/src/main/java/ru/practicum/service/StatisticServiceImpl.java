package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.constant.Constant;
import ru.practicum.dto.StatisticInDto;
import ru.practicum.dto.StatisticViewDto;
import ru.practicum.model.StatisticMapper;
import ru.practicum.exception.StatisticValidationException;
import ru.practicum.repository.StatisticRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statisticRepository;

    @Transactional
    @Override
    public void postHit(StatisticInDto inDto) {
        statisticRepository.save(StatisticMapper.toStatistic(inDto));
        log.info("Сохранение новой записи статистики");
    }

    @Override
    public List<StatisticViewDto> getStatistic(String start, String end, List<String> uris, Boolean unique) {
        LocalDateTime startTime = parseTimeParam(start);
        LocalDateTime endTime = parseTimeParam(end);
        List<StatisticViewDto> dtos;

        if (startTime.isAfter(endTime)) {
            throw new StatisticValidationException(
                    String.format("Старт не может быть позже конца : старт = %s, конец = %s", start, end));
        }

        if (uris != null) {
            if (unique) {
                dtos = statisticRepository.findAllStatisticsByTimeAndListOfUrisAndUniqueIp(startTime, endTime, uris);
            } else {
                dtos = statisticRepository.findAllStatisticsByTimeAndListOfUris(startTime, endTime, uris);
            }
        } else if (unique) {
            dtos = statisticRepository.findAllStatisticsByTimeAndUniqueIp(startTime, endTime);
        } else {
            dtos = statisticRepository.findAllStatisticsByTime(startTime, endTime);
        }
        log.info("Выполнение сбора статистики");
        return dtos;
    }

    private LocalDateTime parseTimeParam(String time) {
        try {
            return LocalDateTime.parse(time, DateTimeFormatter.ofPattern(Constant.TIME_FORMAT));
        } catch (DateTimeParseException e) {
            throw new StatisticValidationException("Передан некорректный формат времени");
        }
    }
}
