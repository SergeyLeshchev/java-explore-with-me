package ru.practicum.ewm.event;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Sort;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;

import java.util.List;

@RestController
@RequestMapping("")
@RequiredArgsConstructor
public class EventController {
    private final EventService eventService;

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getEvents(@PathVariable Long userId,
                                         @RequestParam(required = false, defaultValue = "0") Integer from,
                                         @RequestParam(required = false, defaultValue = "10") Integer size) {
        return eventService.getEvents(userId, from, size);
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @RequestBody @Valid NewEventDto newEventDto) {
        return eventService.addEvent(userId, newEventDto);
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return eventService.getEvent(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto updateEvent(@PathVariable Long userId, @PathVariable Long eventId,
                                    @RequestBody @Valid UpdateEventRequest request) {
        return eventService.updateEvent(userId, eventId, request);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getEventParticipants(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return eventService.getEventParticipants(userId, eventId);
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeRequestStatus(@PathVariable Long userId,
                                                              @PathVariable Long eventId,
                                                              @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        return eventService.changeRequestStatus(userId, eventId, request);
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getEventsAdmin(@RequestParam(required = false) List<Long> users,
                                             @RequestParam(required = false) List<String> states,
                                             @RequestParam(required = false) List<Long> categories,
                                             @RequestParam(required = false) String rangeStart,
                                             @RequestParam(required = false) String rangeEnd,
                                             @RequestParam(required = false, defaultValue = "0") Integer from,
                                             @RequestParam(required = false, defaultValue = "10") Integer size) {
        ListParamsAdmin params = new ListParamsAdmin(users, states, categories, rangeStart, rangeEnd, from, size);
        return eventService.getEventsAdmin(params);
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto updateEventAdmin(@PathVariable Long eventId, @RequestBody @Valid UpdateEventRequest request) {
        return eventService.updateEventAdmin(eventId, request);
    }

    @GetMapping("/events")
    public List<EventShortDto> getEventsPublic(@RequestParam(required = false) String text,
                                               @RequestParam(required = false) List<Long> categories,
                                               @RequestParam(required = false) Boolean paid,
                                               @RequestParam(required = false) String rangeStart,
                                               @RequestParam(required = false) String rangeEnd,
                                               @RequestParam(required = false, defaultValue = "false") Boolean onlyAvailable,
                                               @RequestParam(required = false) Sort sort,
                                               @RequestParam(required = false, defaultValue = "0") Integer from,
                                               @RequestParam(required = false, defaultValue = "10") Integer size,
                                               HttpServletRequest request) {
        ListParamsPublic params = new ListParamsPublic(text, categories, paid,
                rangeStart, rangeEnd, onlyAvailable, sort, from, size, request);
        return eventService.getEventsPublic(params);
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventPublic(@PathVariable Long id, HttpServletRequest request) {
        return eventService.getEventPublic(id, request);
    }
}
