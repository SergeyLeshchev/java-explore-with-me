package ru.practicum.ewm.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    boolean existsByCategoryName(String name);

    @Query(value = """
            SELECT
                e.id,
                e.annotation,
                e.confirmed_requests,
                TO_CHAR(e.event_date, 'YYYY-MM-DD HH24:MI:SS') as event_date,
                e.paid,
                e.title,
                e.views,
                c.id as category_id,
                c.name as category_name,
                u.id as initiator_id,
                u.name as initiator_name
            FROM events e
            LEFT JOIN categories c ON e.category_id = c.id
            LEFT JOIN users u ON e.initiator_id = u.id
            WHERE e.id IN (:ids)
            """, nativeQuery = true)
    List<Object[]> findAllEventShortDtoByIdIn(List<Long> ids);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests - 1 WHERE e.id = :eventId")
    void decrementConfirmedRequests(@Param("eventId") Long eventId);

    @Modifying
    @Transactional
    @Query("UPDATE Event e SET e.confirmedRequests = e.confirmedRequests + 1 WHERE e.id = :eventId")
    void incrementConfirmedRequests(@Param("eventId") Long eventId);

    @Query(value = """
            SELECT
                e.id,
                e.annotation,
                e.confirmed_requests,
                TO_CHAR(e.event_date, 'YYYY-MM-DD HH24:MI:SS') as event_date,
                e.paid,
                e.title,
                e.views,
                c.id as category_id,
                c.name as category_name,
                u.id as initiator_id,
                u.name as initiator_name
            FROM events e
            LEFT JOIN categories c ON e.category_id = c.id
            LEFT JOIN users u ON e.initiator_id = u.id
            WHERE initiator_id = :userId ORDER BY id
            LIMIT :size OFFSET :from
            """, nativeQuery = true)
    List<Object[]> findAllFromAndSizeByUserId(@Param("userId") Long userId,
                                              @Param("from") Integer from,
                                              @Param("size") Integer size);

    @Query(value = """
            SELECT * FROM events e
            WHERE (:users IS NULL OR e.initiator_id IN :users)
              AND (:states IS NULL OR e.state IN :states)
              AND (:categories IS NULL OR e.category_id IN :categories)
              AND e.event_date >= :rangeStart
              AND e.event_date <= :rangeEnd
            ORDER BY e.id
            LIMIT :size OFFSET :from
            """, nativeQuery = true)
    List<Event> findAllByParametersAdmin(@Param("users") List<Long> users,
                                         @Param("states") List<String> states,
                                         @Param("categories") List<Long> categories,
                                         @Param("rangeStart") ZonedDateTime rangeStart,
                                         @Param("rangeEnd") ZonedDateTime rangeEnd,
                                         @Param("from") Integer from,
                                         @Param("size") Integer size);

    @Query(value = """
            SELECT
                e.id,
                e.annotation,
                e.confirmed_requests,
                TO_CHAR(e.event_date, 'YYYY-MM-DD HH24:MI:SS') as event_date,
                e.paid,
                e.title,
                e.views,
                c.id as category_id,
                c.name as category_name,
                u.id as initiator_id,
                u.name as initiator_name
            FROM events e
            LEFT JOIN categories c ON e.category_id = c.id
            LEFT JOIN users u ON e.initiator_id = u.id
            WHERE e.state = 'PUBLISHED'
              AND (LOWER(e.annotation) LIKE LOWER(CONCAT('%', :text, '%')) OR
                   LOWER(e.description) LIKE LOWER(CONCAT('%', :text, '%')))
              AND (COALESCE(:categories, NULL) IS NULL OR e.category_id IN (:categories))
              AND (:paid IS NULL OR e.paid = :paid)
              AND (CAST(:rangeStart AS TIMESTAMP) IS NULL OR e.event_date >= CAST(:rangeStart AS TIMESTAMP))
              AND (CAST(:rangeEnd AS TIMESTAMP) IS NULL OR e.event_date <= CAST(:rangeEnd AS TIMESTAMP))
              AND (:onlyAvailable = false OR
                   e.participant_limit = 0 OR
                   e.confirmed_requests < e.participant_limit)
            ORDER BY
              CASE WHEN :sort = 'EVENT_DATE' THEN e.event_date END,
              CASE WHEN :sort = 'VIEWS' THEN e.views END,
              e.id
            LIMIT :size OFFSET :from
            """, nativeQuery = true)
    List<Object[]> findAllByParametersUser(@Param("text") String text,
                                           @Param("categories") List<Long> categories,
                                           @Param("paid") Boolean paid,
                                           @Param("rangeStart") LocalDateTime rangeStart,
                                           @Param("rangeEnd") LocalDateTime rangeEnd,
                                           @Param("onlyAvailable") Boolean onlyAvailable,
                                           @Param("sort") String sort,
                                           @Param("from") Integer from,
                                           @Param("size") Integer size);
}
