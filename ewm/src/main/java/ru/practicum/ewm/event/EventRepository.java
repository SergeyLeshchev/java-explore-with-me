package ru.practicum.ewm.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryName(String name);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.category " +
            "LEFT JOIN FETCH e.initiator " +
            "WHERE e.id IN :ids")
    List<Event> findAllEventsByIdIn(List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests - 1 WHERE e.id = :eventId")
    void decrementConfirmedRequests(@Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests + 1 WHERE e.id = :eventId")
    void incrementConfirmedRequests(@Param("eventId") Long eventId);

    List<Event> findByInitiatorIdOrderById(Long userId, Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.category " +
            "LEFT JOIN FETCH e.initiator " +
            "WHERE (:users IS NULL OR e.initiator.id IN :users) " +
            "AND (:states IS NULL OR e.state IN :states) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND e.eventDate >= :rangeStart " +
            "AND e.eventDate <= :rangeEnd")
    List<Event> findAllByParametersAdmin(@Param("users") List<Long> users,
                                         @Param("states") List<String> states,
                                         @Param("categories") List<Long> categories,
                                         @Param("rangeStart") ZonedDateTime rangeStart,
                                         @Param("rangeEnd") ZonedDateTime rangeEnd,
                                         Pageable pageable);

    @Query("SELECT e FROM Event e " +
            "LEFT JOIN FETCH e.category " +
            "LEFT JOIN FETCH e.initiator " +
            "WHERE e.state = 'PUBLISHED' " +
            "AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) " +
            "     OR LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%'))) " +
            "AND (:categories IS NULL OR e.category.id IN :categories) " +
            "AND (:paid IS NULL OR e.paid = :paid) " +
            "AND e.eventDate >= :rangeStart " +
            "AND e.eventDate <= :rangeEnd " +
            "AND (:onlyAvailable = false OR e.participantLimit = 0 " +
            "     OR e.confirmedRequests < e.participantLimit)")
    List<Event> findAllByParametersUser(@Param("text") String text,
                                        @Param("categories") List<Long> categories,
                                        @Param("paid") Boolean paid,
                                        @Param("rangeStart") ZonedDateTime rangeStart,
                                        @Param("rangeEnd") ZonedDateTime rangeEnd,
                                        @Param("onlyAvailable") Boolean onlyAvailable,
                                        Pageable pageable);
}
