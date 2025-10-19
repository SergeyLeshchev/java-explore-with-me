package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.repository.StatsRepository;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;

    public Hit createHit(Hit hit) {
        return statsRepository.save(hit);
    }

    public List<StatsResponseDto> getStats(ZonedDateTime start, ZonedDateTime end, List<String> uris, boolean unique) {
        List<Hit> hits;
        if (uris == null || uris.isEmpty()) {
            hits = statsRepository.findAllByTimeHitBetween(start, end);
        } else {
            hits = statsRepository.findAllByUriInAndTimeHitBetween(uris, start, end);
        }

        // Получаем список hits для каждого uri
        Map<String, List<Hit>> hitsForEachUri;
        if (!hits.isEmpty()) {
            hitsForEachUri = hits.stream()
                    .collect(Collectors.groupingBy(Hit::getUri));
        } else {
            hitsForEachUri = new HashMap<>();
        }

        List<StatsResponseDto> statsList = new ArrayList<>();
        if (unique) {
            hitsForEachUri.forEach((uri, hitList) -> {
                        long count = hitList.stream()
                                .map(Hit::getIp)
                                .distinct()
                                .count();
                        statsList.add(new StatsResponseDto(
                                hitList.getFirst().getApp(),
                                uri,
                                (int) count)
                        );
                    }
            );
        } else {
            hitsForEachUri.forEach((uri, hitList) -> statsList.add(new StatsResponseDto(
                            hitList.getFirst().getApp(),
                            uri,
                            hitList.size())
                    )
            );
        }

        return statsList;
    }
}
