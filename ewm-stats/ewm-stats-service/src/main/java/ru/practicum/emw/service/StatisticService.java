package ru.practicum.emw.service;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatisticService {
    EndpointHitDto save(EndpointHitDto createDto);

    List<ViewStatDto> getHits(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}
