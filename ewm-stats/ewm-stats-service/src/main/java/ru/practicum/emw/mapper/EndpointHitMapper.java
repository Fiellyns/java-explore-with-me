package ru.practicum.emw.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.emw.model.EndpointHit;
import ru.practicum.ewm.EndpointHitDto;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface EndpointHitMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", dateFormat = "yyyy-MM-dd HH:mm:ss")
    EndpointHit toHit(EndpointHitDto endpointHitDto);

    EndpointHitDto toDto(EndpointHit endpointHit);
}
