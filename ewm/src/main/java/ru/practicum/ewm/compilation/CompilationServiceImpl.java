package ru.practicum.ewm.compilation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.EventMapper;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (pinned == null) {
            compilations = compilationRepository.findAllFromAndSize(from, size);
        } else {
            compilations = compilationRepository.findAllFromAndSizeAndByPinned(pinned, from, size);
        }
        List<Long> allEventIds = compilations.stream()
                .flatMap(compilation -> compilation.getEvents().stream())
                .distinct()
                .toList();
        List<Object[]> allEventsData = eventRepository.findAllEventShortDtoByIdIn(allEventIds);
        List<EventShortDto> EventShortDtoList = allEventsData.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
        Map<Long, EventShortDto> eventsMap = new HashMap<>();
        EventShortDtoList.forEach(e -> eventsMap.put(e.getId(), e));

        Map<Long, CompilationDto> compilationsDtoMap = new HashMap<>();
        compilations.forEach(c -> compilationsDtoMap.put(c.getId(), CompilationMapper.mapToCompilationDto(c)));
        compilations.forEach(c ->
                c.getEvents().forEach(id ->
                        compilationsDtoMap.get(c.getId()).getEvents().add(eventsMap.get(id))));
        return compilationsDtoMap.values().stream().toList();
    }

    @Override
    public CompilationDto getCompilation(Long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id " + compId + " не найдена"));
        CompilationDto compilationDto = CompilationMapper.mapToCompilationDto(compilation);
        List<Object[]> events = eventRepository.findAllEventShortDtoByIdIn(compilation.getEvents());
        compilationDto.setEvents(events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList());
        return compilationDto;
    }

    @Override
    public CompilationDto saveCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.mapToCompilation(newCompilationDto);
        CompilationDto compilationDto = CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
        List<Object[]> events = eventRepository.findAllEventShortDtoByIdIn(compilation.getEvents());
        compilationDto.setEvents(events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList());
        return compilationDto;
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id " + compId + " не найдена"));
        compilationRepository.deleteById(compId);
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id " + compId + " не найдена"));
        if (updateCompilationRequest.hasEvents()) {
            compilation.setEvents(updateCompilationRequest.getEvents());
        }
        if (updateCompilationRequest.hasPinned()) {
            compilation.setPinned(updateCompilationRequest.getPinned());
        }
        if (updateCompilationRequest.hasTitle()) {
            compilation.setTitle(updateCompilationRequest.getTitle());
        }
        CompilationDto compilationDto = CompilationMapper.mapToCompilationDto(compilationRepository.save(compilation));
        List<Object[]> events = eventRepository.findAllEventShortDtoByIdIn(compilation.getEvents());
        compilationDto.setEvents(events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList());
        return compilationDto;
    }
}
