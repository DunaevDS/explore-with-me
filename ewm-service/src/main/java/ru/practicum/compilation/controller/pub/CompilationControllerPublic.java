package ru.practicum.compilation.controller.pub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/compilations")
public class CompilationControllerPublic {

    private final CompilationService compService;

    @GetMapping
    public List<CompilationDto> findCompilations(@RequestParam(required = false) Boolean pinned,
                                                 @RequestParam(defaultValue = "0") int from,
                                                 @RequestParam(defaultValue = "10") int size) {
        log.info(String.format(
                "Получен запрос GET /compilations на получение списка подборок (pinned = %s, from = %s, size = %s)",
                pinned, from, size));
        return compService.findCompilations(pinned, from, size);
    }

    @GetMapping(path = "/{compId}")
    public CompilationDto findCompilationById(@PathVariable Long compId) {
        log.info(String.format(
                "Получен запрос GET /compilations/{compId} = %s на получение подборки событий", compId));
        return compService.findCompilationById(compId);
    }
}
