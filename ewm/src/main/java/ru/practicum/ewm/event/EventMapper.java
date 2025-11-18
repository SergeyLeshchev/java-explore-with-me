package ru.practicum.ewm.event;

import ru.practicum.ewm.DateTimeMapper;
import ru.practicum.ewm.category.CategoryMapper;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.location.LocationDto;
import ru.practicum.ewm.user.dto.UserShortDto;
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
                event.getViews()
        );
    }

    public static EventShortDto mapToEventShortDto(Object[] result) {
        EventShortDto dto = new EventShortDto();

        dto.setId(((Number) result[0]).longValue());
        dto.setAnnotation((String) result[1]);
        dto.setConfirmedRequests(((Number) result[2]).intValue());
        dto.setEventDate((String) result[3]);
        dto.setPaid((Boolean) result[4]);
        dto.setTitle((String) result[5]);
        dto.setViews(((Number) result[6]).intValue());

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(((Number) result[7]).longValue());
        categoryDto.setName((String) result[8]);
        dto.setCategory(categoryDto);

        UserShortDto userDto = new UserShortDto();
        userDto.setId(((Number) result[9]).longValue());
        userDto.setName((String) result[10]);
        dto.setInitiator(userDto);

        return dto;
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
                dto.getTitle(),
                0
        );
    }
}
