package ru.practicum.emw.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.emw.model.StatisticData;
import ru.practicum.ewm.StatisticCreateDataDto;

@Component
public class StatisticMapper {
    public StatisticCreateDataDto toDto(StatisticData statisticData) {
        return new StatisticCreateDataDto().toBuilder()
                .app(statisticData.getApp())
                .ip(statisticData.getIp())
                .uri(statisticData.getUri())
                .timestamp(statisticData.getTimestamp())
                .build();
    }

    public StatisticData toModel(StatisticCreateDataDto statisticCreateDataDto) {
        return new StatisticData().toBuilder()
                .app(statisticCreateDataDto.getApp())
                .uri(statisticCreateDataDto.getUri())
                .ip(statisticCreateDataDto.getIp())
                .timestamp(statisticCreateDataDto.getTimestamp())
                .build();
    }
}
