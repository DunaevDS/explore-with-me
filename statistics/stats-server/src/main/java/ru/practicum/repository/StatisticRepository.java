package ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.dto.StatisticViewDto;
import ru.practicum.model.Statistic;

import java.time.LocalDateTime;
import java.util.List;


//Сделал как Вы и говорили, но при таком коде валится 1 из тестов и я не понимаю причину
//Тест "В теле ответа должна присутствовать сортировка по убыванию количества просмотров"
@Repository
public interface StatisticRepository extends JpaRepository<Statistic, Long> {
/*    @Query("SELECT new ru.practicum.dto.StatisticViewDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND (s.uri IN :uris OR :uris = NULL) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC ")
    List<StatisticViewDto> findUniqueStatistic(LocalDateTime start,
                                    LocalDateTime end,
                                    List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatisticViewDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN :start AND :end " +
            "AND (s.uri IN :uris OR :uris = NULL) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(DISTINCT s.ip) DESC ")
    List<StatisticViewDto> findStatistic(LocalDateTime start,
                                               LocalDateTime end,
                                               List<String> uris);*/

    @Query("SELECT new ru.practicum.dto.StatisticViewDto(s.app, s.uri, COUNT (s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTime(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatisticViewDto(s.app, s.uri, COUNT (DISTINCT s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTimeAndUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.dto.StatisticViewDto(s.app, s.uri, COUNT (DISTINCT s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTimeAndListOfUrisAndUniqueIp(
            LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.dto.StatisticViewDto(s.app, s.uri, COUNT (s.ip))" +
            "FROM Statistic AS s " +
            "WHERE s.timestamp BETWEEN ?1 AND ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT (s.ip) DESC")
    List<StatisticViewDto> findAllStatisticsByTimeAndListOfUris(LocalDateTime start, LocalDateTime end,
                                                                List<String> uris);
}
