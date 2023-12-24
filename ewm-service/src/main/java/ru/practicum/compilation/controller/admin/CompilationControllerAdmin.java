package ru.practicum.compilation.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/admin/compilations")
public class CompilationControllerAdmin {

    private final CompilationService compService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info(String.format("Получен запрос POST /admin/compilations на добавление новой подборки %s",
                compilationDto.getEvents()));
        return compService.addCompilation(compilationDto);
    }

    @PatchMapping(path = "/{compId}")
    public CompilationDto updateCompilation(@PathVariable Long compId,
                                            @Valid @RequestBody UpdateCompilationRequest updateRequest) {
        log.info(String.format("Получен запрос PATCH /admin/compilations/{compId} = %s на изменение подборки",
                compId));
        return compService.updateCompilation(compId, updateRequest);
    }

    @DeleteMapping(path = "/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        log.info(String.format("Получен запрос DELETE /admin/compilations/{compId} = %s на удаление подборки",
                compId));
        compService.deleteCompilation(compId);
    }
}
