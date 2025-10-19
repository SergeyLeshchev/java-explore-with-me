package ru.practicum.ewm.mapper;

import ru.practicum.ewm.HitRequestDto;
import ru.practicum.ewm.HitResponseDto;
import ru.practicum.ewm.model.Hit;

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
        return new Hit(
                null,
                hitRequestDto.getApp(),
                hitRequestDto.getUri(),
                hitRequestDto.getIp(),
                hitRequestDto.getTimestamp()
        );
    }
}
