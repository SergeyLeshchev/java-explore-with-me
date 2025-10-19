package ru.practicum.ewm.service;

import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {
    Hit createHit(Hit hit);

    List<StatsResponseDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}
