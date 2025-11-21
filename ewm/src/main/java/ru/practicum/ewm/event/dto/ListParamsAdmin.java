package ru.practicum.ewm.event.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.DateTimeMapper;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ListParamsAdmin {
    private final List<Long> users;
    private final List<String> states;
    private final List<Long> categories;
    private final ZonedDateTime rangeStart;
    private final ZonedDateTime rangeEnd;
    private final Integer from;
    private final Integer size;

    public ListParamsAdmin(List<Long> users, List<String> states, List<Long> categories,
                           String rangeStart, String rangeEnd, Integer from, Integer size) {
        this.users = users;
        this.states = states;
        this.categories = categories;
        this.rangeStart = Objects.requireNonNullElse(
                DateTimeMapper.mapToZonedDateTime(rangeStart),
                ZonedDateTime.now(ZoneOffset.UTC)
        );
        // Константа MAX задает слишком большое значение и возникает ошибка. Беру интервал на 100 лет вперед
        this.rangeEnd = Objects.requireNonNullElse(
                DateTimeMapper.mapToZonedDateTime(rangeEnd),
                ZonedDateTime.now(ZoneOffset.UTC).plusYears(100)
        );
        this.from = from;
        this.size = size;
    }
}
