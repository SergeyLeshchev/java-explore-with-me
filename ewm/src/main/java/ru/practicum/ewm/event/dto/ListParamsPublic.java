package ru.practicum.ewm.event.dto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.ewm.event.model.Sort;

import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ListParamsPublic {
    private final String text;
    private final List<Long> categories;
    private final Boolean paid;
    private final String rangeStart;
    private final String rangeEnd;
    private final Boolean onlyAvailable;
    private final Sort sort;
    private final Integer from;
    private final Integer size;
    private final HttpServletRequest request;

    public ListParamsPublic(String text, List<Long> categories, Boolean paid, String rangeStart, String rangeEnd,
                            Boolean onlyAvailable, Sort sort, Integer from, Integer size, HttpServletRequest request) {
        this.text = Objects.requireNonNullElse(text, "");
        this.categories = categories;
        this.paid = paid;
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
        this.onlyAvailable = onlyAvailable;
        this.sort = Objects.requireNonNullElse(sort, Sort.ID);
        this.from = from;
        this.size = size;
        this.request = request;
    }
}
