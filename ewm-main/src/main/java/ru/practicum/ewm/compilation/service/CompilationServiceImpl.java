package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {

    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        if (events.size() < newCompilationDto.getEvents().size()) {
            throw new NotFoundException("Не все события присутствуют!");
        }
        Compilation compilation = compilationRepository.save(compilationMapper.toCompilation(newCompilationDto, events));

        return compilationMapper.toDto(compilation);
    }

    @Transactional
    @Override
    public void delete(Long compId) {
        compilationRepository.deleteById(compId);
    }

    @Transactional
    @Override
    public CompilationDto update(long compId, UpdateCompilationDto dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id:" + compId + " не найдена"));
        List<Event> events = eventRepository.findAllByIdIn(dto.getEvents());
        if (events.size() < dto.getEvents().size()) {
            throw new NotFoundException("Не все события присутствуют!");
        }
        compilation.setEvents(events);
        Compilation updated = compilationRepository.save(compilationMapper.update(dto, compilation));

        return compilationMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest) {
        List<Compilation> compilations;
        compilations = (pinned == null) ? compilationRepository.findAll(pageRequest).getContent() :
                compilationRepository.findAllByPinned(pinned, pageRequest);
        return compilationMapper.toDtoList(compilations);
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Подборка событий с id:" + compId + " не найдена"));
        return compilationMapper.toDto(compilation);
    }
}
