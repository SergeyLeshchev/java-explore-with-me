package ru.practicum.ewm.service;

import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.Hit;

import java.time.ZonedDateTime;
import java.util.List;

public interface StatsService {
    Hit createHit(HitRequestDto newHitDto);

    List<ViewStatsDto> getStats(ZonedDateTime start, ZonedDateTime end, List<String> uris, boolean unique);
}
