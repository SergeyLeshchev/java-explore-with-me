package ru.practicum.ewm.event;

import ru.practicum.ewm.DateTimeMapper;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.LocationDto;
import ru.practicum.ewm.user.mapper.UserMapper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class EventMapper {
    public static EventFullDto mapToEventFullDto(Event event) {
        return new EventFullDto(
                event.getAnnotation(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                DateTimeMapper.mapToString(event.getCreatedOn()),
                event.getDescription(),
                DateTimeMapper.mapToString(event.getEventDate()),
                event.getId(),
                UserMapper.mapToUserShortDto(event.getInitiator()),
                new LocationDto(event.getLocation().getLat(), event.getLocation().getLon()),
                event.getPaid(),
                event.getParticipantLimit(),
                DateTimeMapper.mapToString(event.getPublishedOn()),
                event.getRequestModeration(),
                event.getState().toString(),
                event.getTitle(),
                // Ставим null, чтобы можно было легко увидеть, что полям еще не присваивали значения
                null,
                null
        );
    }

    public static EventShortDto mapToEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.mapToCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                DateTimeMapper.mapToString(event.getEventDate()),
                event.getId(),
                UserMapper.mapToUserShortDto(event.getInitiator()),
                event.getPaid(),
                event.getTitle(),
                null
        );
    }

    public static Event mapToEvent(NewEventDto dto) {
        return new Event(
                null,
                dto.getAnnotation(),
                null,
                0,
                ZonedDateTime.now(ZoneOffset.UTC),
                dto.getDescription(),
                DateTimeMapper.mapToZonedDateTime(dto.getEventDate()),
                null,
                null,
                dto.getPaid(),
                dto.getParticipantLimit(),
                null,
                dto.getRequestModeration(),
                null,
                dto.getTitle()
        );
    }
}
