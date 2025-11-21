package ru.practicum.ewm.user;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    List<ParticipationRequestDto> getUserRequests(Long userId);

    ParticipationRequestDto addParticipationRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequest(Long userId, Long requestId);

    List<UserDto> getUsers(List<Long> ids, Integer from, Integer size);

    UserDto registerUser(NewUserRequest newUserRequest);

    void deleteUser(Long userId);
}
