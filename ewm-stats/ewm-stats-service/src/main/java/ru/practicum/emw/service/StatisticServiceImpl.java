package ru.practicum.emw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.emw.mapper.EndpointHitMapper;
import ru.practicum.emw.repository.StatisticRepository;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    private final StatisticRepository statRepository;
    private final EndpointHitMapper mapper;

    @Transactional
    @Override
    public EndpointHitDto save(EndpointHitDto createDto) {
        return mapper.toDto(statRepository.save(mapper.toHit(createDto)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Начальное время должно быть раньше конечного!");
        }
        if (unique) {
            return statRepository.findAllUniqueHitByTimeBetween(start, end, uris);
        } else {
            return statRepository.findAllByTimeBetween(start, end, uris);
        }
    }
}
