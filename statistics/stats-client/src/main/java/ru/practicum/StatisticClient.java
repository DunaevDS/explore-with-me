package ru.practicum;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.dto.StatisticInDto;
import ru.practicum.dto.StatisticRequestDto;

import java.util.Map;

@Slf4j
@Service
public class StatisticClient extends BaseClient {

    @Autowired
    public StatisticClient(@Value("${stats-server.url}") String serviceUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serviceUrl + ""))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> postHit(StatisticInDto inDto) {
        log.info("зашли в posthit");
        log.info("inDto = " + inDto);
        log.info("вышли из posthit");
        return post("/hit", inDto);
    }

    public ResponseEntity<Object> getStatistics(StatisticRequestDto requestDto) {
        Map<String, Object> params = Map.of(
                "start", requestDto.getStart(),
                "end", requestDto.getEnd(),
                "uris", requestDto.getUris(),
                "unique", requestDto.getUnique()
        );
        return get("/stats?start={start}&end={end}&uris=uris&unique={unique}", params);
    }


}
