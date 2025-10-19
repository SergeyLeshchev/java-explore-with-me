package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.StatsResponseDto;
import ru.practicum.ewm.mapper.HitMapper;
import ru.practicum.ewm.model.Hit;
import ru.practicum.ewm.service.StatsService;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

@RestController
@RequestMapping()
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitResponseDto createHit(@RequestBody HitRequestDto newHitDto) {
        Hit newHit = statsService.createHit(HitMapper.mapToHit(newHitDto));
        HitResponseDto hitResponseDto = HitMapper.mapToHitResponseDto(newHit);
        return hitResponseDto;
    }

    @GetMapping(path = "/stats")
    public List<StatsResponseDto> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                           @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                           @RequestParam(required = false) List<String> uris,
                                           @RequestParam(defaultValue = "false") boolean unique) {
        return statsService.getStats(start.atZone(ZoneOffset.UTC), end.atZone(ZoneOffset.UTC), uris, unique);
    }

    @GetMapping("/check")
    public String check() {
        return "Это улучшенная версия контейнеров 8";
    }
}
