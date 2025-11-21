package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CompilationDto {
    private List<EventShortDto> events;
    private Long id;
    private Boolean pinned;
    @NotBlank
    private String title;
}
