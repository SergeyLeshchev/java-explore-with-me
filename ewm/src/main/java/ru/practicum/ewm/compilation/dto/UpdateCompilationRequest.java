package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UpdateCompilationRequest {
    private List<Long> events;
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;

    public boolean hasEvents() {
        return !(events == null || events.isEmpty());
    }

    public boolean hasPinned() {
        return !(pinned == null);
    }

    public boolean hasTitle() {
        return !(title == null || title.isBlank());
    }
}
