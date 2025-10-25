package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.model.StatsProjection;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Hit, Long> {
    // Все URI, уникальные IP
    @Query("SELECT h.app as app, h.uri as uri, COUNT(DISTINCT h.ip) as hits FROM Hit h " +
            "WHERE h.timeHit BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri")
    List<StatsProjection> findAllUrisUniqueIp(ZonedDateTime start, ZonedDateTime end);

    // Все URI, НЕ уникальные IP
    @Query("SELECT h.app as app, h.uri as uri, COUNT(h) as hits FROM Hit h " +
            "WHERE h.timeHit BETWEEN :start AND :end " +
            "GROUP BY h.app, h.uri")
    List<StatsProjection> findAllUrisNotUniqueIp(ZonedDateTime start, ZonedDateTime end);

    // Уникальные IP с фильтром по URI
    @Query("SELECT h.app as app, h.uri as uri, COUNT(DISTINCT h.ip) as hits FROM Hit h " +
            "WHERE h.timeHit BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri")
    List<StatsProjection> findByUriInUniqueIp(List<String> uris, ZonedDateTime start, ZonedDateTime end);

    // Все хиты с фильтром по URI
    @Query("SELECT h.app as app, h.uri as uri, COUNT(h) as hits FROM Hit h " +
            "WHERE h.timeHit BETWEEN :start AND :end " +
            "AND h.uri IN :uris " +
            "GROUP BY h.app, h.uri")
    List<StatsProjection> findByUriInNotUniqueIp(List<String> uris, ZonedDateTime start, ZonedDateTime end);
}
