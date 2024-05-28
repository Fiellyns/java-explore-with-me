package ru.practicum.ewm;

import java.util.List;

public interface StatisticClient {
    void postHit(EndpointHitDto endpointHitDto);

    List<ViewStatDto> getStatistics(String start, String end, List<String> uris, Boolean unique);
}
