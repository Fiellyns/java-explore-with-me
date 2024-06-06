package ru.practicum.ewm.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto save(NewCompilationDto dto);

    void delete(Long compId);

    CompilationDto update(long compId, UpdateCompilationDto dto);

    List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest);

    CompilationDto getById(long compId);
}
