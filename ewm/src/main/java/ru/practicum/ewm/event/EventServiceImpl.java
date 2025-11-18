package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.DateTimeMapper;
import ru.practicum.ewm.StatsClient;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.category.CategoryRepository;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.Sort;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.Status;
import ru.practicum.ewm.exception.BadRequestException;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.location.Location;
import ru.practicum.ewm.location.LocationRepository;
import ru.practicum.ewm.user.ParticipationRequestRepository;
import ru.practicum.ewm.user.UserRepository;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.user.model.ParticipationRequest;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ParticipationRequestRepository participationRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        List<Object[]> events = eventRepository.findAllFromAndSizeByUserId(userId, from, size);
        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto newEventDto) {
        if (DateTimeMapper.mapToZonedDateTime(newEventDto.getEventDate())
                .isBefore(ZonedDateTime.now(ZoneOffset.UTC).plusHours(2))) {
            throw new BadRequestException("Событие должно начинаться не раньше чем " +
                    "через 2 часа от настоящего момента");
        }
        Event event = EventMapper.mapToEvent(newEventDto);
        event.setCategory(categoryRepository.findById(newEventDto.getCategory())
                .orElseThrow(() -> new NotFoundException("Категория с id " +
                        newEventDto.getCategory() + " не найдена")));
        event.setInitiator(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден")));
        Location location = locationRepository.save(new Location(
                null,
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon())
        );
        event.setLocation(location);
        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }
        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }
        event.setState(State.PENDING);
        event.setPublishedOn(ZonedDateTime.now(ZoneOffset.UTC));
        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        return EventMapper.mapToEventFullDto(event);
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest request) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        if (event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Изменить можно только отмененные события " +
                    "или события в состоянии ожидания модерации");
        }
        if (request.hasEventDate()) {
            ZonedDateTime time = DateTimeMapper.mapToZonedDateTime(request.getEventDate());
            if (time.isBefore(ZonedDateTime.now(ZoneOffset.UTC).plusHours(2))) {
                throw new BadRequestException("Событие должно начинаться не раньше чем " +
                        "через 2 часа от настоящего момента");
            }
            event.setEventDate(time);
        }
        updateEventFields(event, request);
        if (request.hasStateAction()) {
            if ("SEND_TO_REVIEW".equals(request.getStateAction())) {
                event.setState(State.PENDING);
            } else if ("CANCEL_REVIEW".equals(request.getStateAction())) {
                event.setState(State.CANCELED);
            } else {
                throw new BadRequestException("Событие можно только отправить на модерацию или отменить");
            }
        }
        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        return participationRepository.findAllByEvent(eventId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId,
                                                              EventRequestStatusUpdateRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        List<ParticipationRequest> requests = participationRepository.findAllById(request.getRequestIds());
        if (requests.stream().anyMatch(r -> !r.getStatus().equals(Status.PENDING))) {
            throw new ConflictException("Статус можно изменить только у заявок, находящихся в состоянии ожидания");
        }
        if (Status.valueOf(request.getStatus()).equals(Status.CONFIRMED) &&
                event.getParticipantLimit() != 0 &&
                event.getConfirmedRequests() == event.getParticipantLimit()) {
            throw new ConflictException("Достигнут лимит по заявкам на данное событие");
        }

        List<ParticipationRequestDto> confirmed = new ArrayList<>();
        List<ParticipationRequestDto> rejected = new ArrayList<>();
        Status status = Status.valueOf(request.getStatus());
        if (status.equals(Status.REJECTED)) {
            requests.forEach(r -> {
                r.setStatus(status);
                rejected.add(ParticipationRequestMapper.mapToParticipationRequestDto(r));
            });
        } else if (status.equals(Status.CONFIRMED)) {
            int num = event.getConfirmedRequests();
            int max = event.getParticipantLimit();
            for (ParticipationRequest r : requests) {
                if (num < max) {
                    r.setStatus(status);
                    confirmed.add(ParticipationRequestMapper.mapToParticipationRequestDto(r));
                } else {
                    r.setStatus(Status.REJECTED);
                    rejected.add(ParticipationRequestMapper.mapToParticipationRequestDto(r));
                }
                num++;
            }
            event.setConfirmedRequests(num);
        } else {
            throw new ConflictException("Заявки можно только отклонить или одобрить");
        }
        eventRepository.save(event);
        participationRepository.saveAll(requests);
        return new EventRequestStatusUpdateResult(confirmed, rejected);
    }

    @Override
    public List<EventFullDto> getEvents_2(List<Long> users, List<String> states, List<Long> categories,
                                          String rangeStart, String rangeEnd, Integer from, Integer size) {
        if (users == null) {
            users = Collections.emptyList();
        }
        if (states == null) {
            states = Collections.emptyList();
        }
        if (categories == null) {
            categories = Collections.emptyList();
        }
        if (rangeStart == null || rangeStart.isBlank()) {
            rangeStart = "2020-01-01 01:00:00";
        }
        if (rangeEnd == null || rangeEnd.isBlank()) {
            rangeEnd = "2040-01-01 01:00:00";
        }
        List<Event> events = eventRepository.findAllByParametersAdmin(
                users, states, categories,
                DateTimeMapper.mapToZonedDateTime(rangeStart),
                DateTimeMapper.mapToZonedDateTime(rangeEnd),
                from, size);
        return events.stream()
                .map(EventMapper::mapToEventFullDto)
                .toList();
    }

    @Override
    public EventFullDto updateEvent_1(Long eventId, UpdateEventRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        if (request.hasEventDate()) {
            ZonedDateTime time = DateTimeMapper.mapToZonedDateTime(request.getEventDate());
            if (time.isBefore(ZonedDateTime.now(ZoneOffset.UTC).plusHours(1))) {
                throw new BadRequestException("Событие должно начинаться не раньше чем " +
                        "через 1 час от настоящего момента");
            }
            event.setEventDate(time);
        }
        if (!event.getState().equals(State.PENDING)) {
            throw new ConflictException("Событие должно быть в состоянии ожидания публикации");
        }

        updateEventFields(event, request);
        if (request.hasStateAction()) {
            if ("PUBLISH_EVENT".equals(request.getStateAction())) {
                event.setState(State.PUBLISHED);
            } else if ("REJECT_EVENT".equals(request.getStateAction())) {
                event.setState(State.CANCELED);
            } else {
                throw new BadRequestException("Событие можно только опубликовать или отменить");
            }
        }
        return EventMapper.mapToEventFullDto(eventRepository.save(event));
    }

    @Override
    public List<EventShortDto> getEvents_1(String text, List<Long> categories, Boolean paid,
                                           String rangeStart, String rangeEnd, Boolean onlyAvailable,
                                           Sort sort, Integer from, Integer size, HttpServletRequest request) {
        if (text == null) {
            text = "";
        }
        if (categories == null) {
            categories = Collections.emptyList();
        }

        // Проверяем на null и конвертируем в LocalDateTime
        LocalDateTime start = null;
        LocalDateTime end = null;
        // Если период не задан, то ищем от настоящего момента
        if ((rangeStart == null || rangeStart.isBlank()) &&
                (rangeEnd == null || rangeEnd.isBlank())) {
            start = LocalDateTime.now();
        } else {
            if (rangeStart != null) {
                start = LocalDateTime.parse(rangeStart, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            if (rangeEnd != null) {
                end = LocalDateTime.parse(rangeEnd, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
        }
        if (start != null && end != null) {
            if (end.isBefore(start)) {
                throw new BadRequestException("Начало событие не может быть позже его окончания");
            }
        }
        String sortString = "";
        if (sort != null) {
            sortString = sort.toString();
        }

        List<Object[]> events = eventRepository.findAllByParametersUser(text, categories, paid,
                start, end, onlyAvailable, sortString, from, size);

        statsClient.createHit(request);
        return events.stream()
                .map(EventMapper::mapToEventShortDto)
                .toList();
    }

    @Override
    public EventFullDto getEvent_1(Long eventId, HttpServletRequest request) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Событие с id " + eventId + " должно быть опубликовано.");
        }

        statsClient.createHit(request);

        List<ViewStatsDto> stats = statsClient.getStats("2020-01-01 01:00:00", "2040-01-01 01:00:00",
                List.of(request.getRequestURI()), true);

        if (stats != null && !stats.isEmpty()) {
            event.setViews(stats.getFirst().getHits());
        } else {
            event.setViews(0);
        }
        return EventMapper.mapToEventFullDto(event);
    }

    private void updateEventFields(Event event, UpdateEventRequest request) {
        if (request.hasAnnotation()) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.hasCategory()) {
            if (event.getCategory() != null) {
                // Обращаемся к БД только в том случае, если у обновленного события другая категория
                if (!event.getCategory().getId().equals(request.getCategory())) {
                    event.setCategory(
                            categoryRepository.findById(request.getCategory())
                                    .orElseThrow(() -> new NotFoundException("Категория с id " +
                                            request.getCategory() + " не найдена"))
                    );
                }
            }
        }
        if (request.hasDescription()) {
            event.setDescription(request.getDescription());
        }
        if (request.hasLocation()) {
            // Обращаемся к БД только в том случае, если у обновленного события другая локация
            if (!(event.getLocation().getLat().equals(request.getLocation().getLat()) &&
                    event.getLocation().getLon().equals(request.getLocation().getLon()))) {
                event.getLocation().setLat(request.getLocation().getLat());
                event.getLocation().setLon(request.getLocation().getLon());
                locationRepository.save(event.getLocation());
            }
        }
        if (request.hasPaid()) {
            event.setPaid(request.getPaid());
        }
        if (request.hasParticipantLimit()) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.hasRequestModeration()) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.hasTitle()) {
            event.setTitle(request.getTitle());
        }
    }
}
