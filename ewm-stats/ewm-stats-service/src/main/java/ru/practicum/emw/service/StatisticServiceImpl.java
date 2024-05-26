package ru.practicum.emw.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.emw.mapper.StatisticMapper;
import ru.practicum.emw.repository.StatisticRepository;
import ru.practicum.ewm.StatisticCreateDataDto;
import ru.practicum.ewm.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticServiceImpl implements StatisticService {

    public final StatisticRepository statRepository;
    private final StatisticMapper mapper;

    @Override
    public StatisticCreateDataDto save(StatisticCreateDataDto createDto) {
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
