package ru.practicum.emw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.emw.mapper.EndpointHitMapper;
import ru.practicum.emw.repository.StatisticRepository;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    public final StatisticRepository statRepository;
    private final EndpointHitMapper mapper;

    @Override
    public EndpointHitDto save(EndpointHitDto createDto) {
        return mapper.toDto(statRepository.save(mapper.toModel(createDto)));
    }

    @Override
    public List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return statRepository.findAllUniqueHitByTimeBetween(start, end, uris);
        } else {
            return statRepository.findAllByTimeBetween(start, end, uris);
        }
    }
}
