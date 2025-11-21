package ru.practicum.ewm.user;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.model.ParticipationRequest;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {
    List<ParticipationRequest> findAllByRequester(Long userId);

    boolean existsByRequesterAndEvent(Long requester, Long event);

    List<ParticipationRequest> findAllByEvent(Long eventId);
}
