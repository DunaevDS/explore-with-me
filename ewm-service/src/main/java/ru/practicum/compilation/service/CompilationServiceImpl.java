package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.CompilationMapper;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.service.EventStatService;
import ru.practicum.exception.CompilationNotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final EventStatService statService;

    @Override
    public List<CompilationDto> findCompilations(boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.findAllByIsPinned(pinned, pageable);
        log.info("compilations = " + compilations);
        List<Long> eventIds = compilations.stream()
                .map(Compilation::getEvents)
                .flatMap(Collection::stream)
                .map(Event::getId)
                .collect(Collectors.toList());
        log.info("eventIds = " + eventIds);
        Map<Long, Long> views = statService.getEventsViews(eventIds);
        log.info("views=" + views);
        log.info("Выполнен запрос на поиск подборок событий");
        return CompilationMapper.toDtos(compilations, views);
    }

    @Override
    public CompilationDto findCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Подборка с id " + compId + " не найдена"));
        List<Long> events = compilation.getEvents().stream().map(Event::getId).collect(Collectors.toList());
        Map<Long, Long> views = statService.getEventsViews(events);
        log.info("Выполнен поиск подборки событий по id {}", compId);
        return CompilationMapper.toCompilationDto(compilation, views);
    }

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        List<Event> events;
        Map<Long, Long> views = new HashMap<>();
        if (compilationDto.getEvents() != null) {
            events = eventRepository.findAllByIdIn(compilationDto.getEvents());
            views = statService.getEventsViews(events.stream().map(Event::getId).collect(Collectors.toList()));
        } else {
            events = new ArrayList<>();
        }
        Compilation compilation = CompilationMapper.toNewCompilation(compilationDto, events);
        compilation = compilationRepository.save(compilation);
        log.info("Добавлена новая подборка событий");
        return CompilationMapper.toCompilationDto(compilation, views);
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
            log.info("Подборка с id " + compId + "удалена");
        } else throw new CompilationNotFoundException("Подборка с id " + compId + " не найдена");
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest update) {
        Compilation oldCompilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new CompilationNotFoundException("Подборка с id " + compId + " не найдена"));
        Map<Long, Long> views = new HashMap<>();
        if (update.getEvents() != null && !update.getEvents().isEmpty()) {

            List<Event> events = eventRepository.findAllByIdIn(update.getEvents());
            oldCompilation.setEvents(events);
            views = statService.getEventsViews(update.getEvents());
        }
        if (update.getPinned() != null) {
            oldCompilation.setIsPinned(update.getPinned());
        }
        if (update.getTitle() != null) {
            oldCompilation.setTitle(update.getTitle());
        }
        log.info("Подборка с id {} была обновлена", compId);
        return CompilationMapper.toCompilationDto(compilationRepository.save(oldCompilation), views);
    }
}
