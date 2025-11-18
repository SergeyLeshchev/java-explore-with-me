package ru.practicum.ewm.user.mapper;

import ru.practicum.ewm.user.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.model.ParticipationRequest;

import java.time.format.DateTimeFormatter;

public class ParticipationRequestMapper {
    public static ParticipationRequestDto mapToParticipationRequestDto(ParticipationRequest request) {
        return new ParticipationRequestDto(
                request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")),
                request.getEvent(),
                request.getId(),
                request.getRequester(),
                request.getStatus().toString()
        );
    }
}
