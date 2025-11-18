package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Sort;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;

import java.util.List;

@Service
public interface EventService {
    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto addEvent(Long userId, NewEventDto newEventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventRequest request);

    List<ParticipationRequestDto> getEventParticipants(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    List<EventFullDto> getEvents_2(List<Long> users, List<String> states, List<Long> categories,
                                   String rangeStart, String rangeEnd, Integer from, Integer size);

    EventFullDto updateEvent_1(Long eventId, UpdateEventRequest request);

    List<EventShortDto> getEvents_1(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                                    Boolean onlyAvailable, Sort sort, Integer from, Integer size,
                                    HttpServletRequest request);

    EventFullDto getEvent_1(Long eventId, HttpServletRequest request);
}
