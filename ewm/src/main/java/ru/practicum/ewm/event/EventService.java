package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.dto.*;
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

    List<EventFullDto> getEventsAdmin(ListParamsAdmin params);

    EventFullDto updateEventAdmin(Long eventId, UpdateEventRequest request);

    List<EventShortDto> getEventsPublic(ListParamsPublic params);

    EventFullDto getEventPublic(Long eventId, HttpServletRequest request);
}
