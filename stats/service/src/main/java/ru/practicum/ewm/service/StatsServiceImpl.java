package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.ViewStatsDto;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.model.StatsProjection;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public Hit createHit(HitRequestDto newHitDto) {
        return statsRepository.save(new Hit(
                null,
                newHitDto.getApp(),
                newHitDto.getUri(),
                newHitDto.getIp(),
                ZonedDateTime.now(ZoneOffset.UTC)
        ));
    }

    public List<ViewStatsDto> getStats(ZonedDateTime start, ZonedDateTime end, List<String> uris, boolean unique) {
        List<StatsProjection> statsProjections;
        if (uris == null || uris.isEmpty()) {
            if (unique) {
                statsProjections = statsRepository.findAllUrisUniqueIp(start, end);
            } else {
                statsProjections = statsRepository.findAllUrisNotUniqueIp(start, end);
            }
        } else {
            if (unique) {
                statsProjections = statsRepository.findByUriInUniqueIp(uris, start, end);
            } else {
                statsProjections = statsRepository.findByUriInNotUniqueIp(uris, start, end);
            }
        }

        return statsProjections.stream()
                .map(sp -> new ViewStatsDto(
                        sp.getApp(),
                        sp.getUri(),
                        sp.getHits().intValue()
                ))
                .sorted((a, b) -> Integer.compare(b.getHits(), a.getHits()))
                .toList();
    }
}
