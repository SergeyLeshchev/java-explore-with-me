package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Hit;

import java.time.ZonedDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<Hit, Long> {
    List<Hit> findAllByTimeHitBetween(ZonedDateTime start, ZonedDateTime end);

    List<Hit> findAllByUriInAndTimeHitBetween(
            List<String> uris, ZonedDateTime start, ZonedDateTime end);
}
