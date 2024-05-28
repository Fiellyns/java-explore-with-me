package ru.practicum.emw.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.emw.model.EndpointHit;
import ru.practicum.ewm.EndpointHitDto;

@Component
public class EndpointHitMapper {
    public EndpointHitDto toDto(EndpointHit endpointHit) {
        return new EndpointHitDto().toBuilder()
                .app(endpointHit.getApp())
                .ip(endpointHit.getIp())
                .uri(endpointHit.getUri())
                .timestamp(endpointHit.getTimestamp())
                .build();
    }

    public EndpointHit toModel(EndpointHitDto endpointHitDto) {
        return new EndpointHit().toBuilder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .timestamp(endpointHitDto.getTimestamp())
                .build();
    }
}
