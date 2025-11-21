package ru.practicum.ewm.compilation;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;

import java.util.ArrayList;

public class CompilationMapper {
    public static CompilationDto mapToCompilationDto(Compilation compilation) {
        return new CompilationDto(
                new ArrayList<>(),
                compilation.getId(),
                compilation.getPinned(),
                compilation.getTitle()
        );
    }

    public static Compilation mapToCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = new Compilation(
                null,
                newCompilationDto.getEvents(),
                newCompilationDto.getPinned(),
                newCompilationDto.getTitle()
        );
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }
        return compilation;
    }
}
