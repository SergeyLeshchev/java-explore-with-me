package ru.practicum.ewm.mapper;

import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.model.Hit;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class HitMapper {
    public static HitResponseDto mapToHitResponseDto(Hit hit) {
        return new HitResponseDto(
                hit.getId(),
                hit.getApp(),
                hit.getUri(),
                hit.getIp(),
                hit.getTimeHit().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

    public static Hit mapToHit(HitRequestDto hitRequestDto) {
        LocalDateTime localDateTime = LocalDateTime.parse(hitRequestDto.getTimestamp(),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        return new Hit(
                null,
                hitRequestDto.getApp(),
                hitRequestDto.getUri(),
                hitRequestDto.getIp(),
                localDateTime.atZone(ZoneOffset.UTC)
        );
    }
}
