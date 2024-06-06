package ru.practicum.ewm.compilation.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.repository.RequestRepository;

import java.util.List;

@Service
public class CompilationServiceImpl implements CompilationService {

    private final CompilationMapper compilationMapper;
    private final EventRepository eventRepository;
    private final RequestRepository requestRepository;
    private final EventMapper eventMapper;
    public CompilationRepository compilationRepository;

    @Autowired
    public CompilationServiceImpl(CompilationRepository compilationRepository, CompilationMapper compilationMapper, EventRepository eventRepository, RequestRepository requestRepository, EventMapper eventMapper) {
        this.compilationRepository = compilationRepository;
        this.compilationMapper = compilationMapper;
        this.eventRepository = eventRepository;
        this.requestRepository = requestRepository;
        this.eventMapper = eventMapper;
    }

    @Transactional
    @Override
    public CompilationDto save(NewCompilationDto newCompilationDto) {
        List<Event> events = eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        if (events.size() < newCompilationDto.getEvents().size()) {
            throw new NotFoundException("Не все события присутствуют!");
        }
        Compilation compilation = compilationRepository.save(
                compilationMapper.toCompilation(newCompilationDto, events));

        return compilationMapper.toDto(
                compilation);
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
        Compilation updated = compilationRepository.save(
                compilationMapper.update(dto, compilation));

        return compilationMapper.toDto(updated);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getAll(Boolean pinned, PageRequest pageRequest) {
        List<Compilation> compilations = compilationRepository.findAllByPinned(pinned, pageRequest);
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
