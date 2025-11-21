package ru.practicum.ewm.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.EventRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.State;
import ru.practicum.ewm.event.model.Status;
import ru.practicum.ewm.exception.ConflictException;
import ru.practicum.ewm.exception.DataAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.UniqueConstraintException;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.ParticipationRequestMapper;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.ParticipationRequest;
import ru.practicum.ewm.user.model.User;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ParticipationRequestRepository requestRepository;
    private final EventRepository eventRepository;

    @Override
    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        return requestRepository.findAllByRequester(userId).stream()
                .map(ParticipationRequestMapper::mapToParticipationRequestDto)
                .toList();
    }

    @Override
    public ParticipationRequestDto addParticipationRequest(Long userId, Long eventId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id " + eventId + " не найдено"));
        if (requestRepository.existsByRequesterAndEvent(userId, eventId)) {
            throw new ConflictException("Запрос пользователя с id " + userId +
                    " и событием с id " + eventId + " уже существует.");
        }
        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор события не может добавить запрос на участие в своём событии");
        }
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Нельзя участвовать в неопубликованном событии");
        }
        if ((event.getParticipantLimit() != 0) && (event.getParticipantLimit() <= event.getConfirmedRequests())) {
            throw new ConflictException("У события достигнут лимит запросов на участие");
        }

        ParticipationRequest request = new ParticipationRequest(
                null,
                ZonedDateTime.now(ZoneOffset.UTC),
                eventId,
                userId,
                Status.PENDING
        );

        // Если модерация не требуется, сразу подтверждаем
        if ((!event.getRequestModeration()) || (event.getParticipantLimit() == 0)) {
            request.setStatus(Status.CONFIRMED);
            ParticipationRequest savedRequest = requestRepository.save(request);
            eventRepository.incrementConfirmedRequests(eventId);
            return ParticipationRequestMapper.mapToParticipationRequestDto(savedRequest);
        }

        // Если модерация требуется, оставляем PENDING
        return ParticipationRequestMapper.mapToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public ParticipationRequestDto cancelRequest(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        ParticipationRequest request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка с id " + requestId + " не найдена"));
        if (!request.getRequester().equals(userId)) {
            throw new DataAccessException("Пользователь может отменить только свою заявку");
        }
        if (request.getStatus().equals(Status.CONFIRMED)) {
            eventRepository.decrementConfirmedRequests(request.getEvent());
        }
        request.setStatus(Status.CANCELED);
        return ParticipationRequestMapper.mapToParticipationRequestDto(requestRepository.save(request));
    }

    @Override
    public List<UserDto> getUsers(List<Long> ids, Integer from, Integer size) {
        List<User> users;
        if (ids == null || ids.isEmpty()) {
            users = userRepository.findAllFromAndSize(from, size);
        } else {
            users = userRepository.findAllByIdIn(ids);
        }
        return users.stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto registerUser(NewUserRequest newUserRequest) {
        if (userRepository.existsByEmail(newUserRequest.getEmail())) {
            throw new UniqueConstraintException("Пользователь с почтой " + newUserRequest.getEmail() + " уже существует");
        }
        User user = UserMapper.mapToUser(newUserRequest);
        return UserMapper.mapToUserDto(userRepository.save(user));
    }

    @Override
    public void deleteUser(Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + userId + " не найден"));
        userRepository.deleteById(userId);
    }
}
