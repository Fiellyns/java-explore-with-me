package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING,
        imports = {LocalDateTime.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        uses = UserMapper.class)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "createdOn", expression = "java(LocalDateTime.now().withNano(0))")
    Event toModel(NewEventDto eventDto, User initiator, Category category, EventState state);


    @Mapping(target = "id", ignore = true)
    Event update(UpdateEventUserRequest updateEventDto, @MappingTarget Event event);

    @Mapping(target = "id", ignore = true)
    Event update(UpdateEventAdminRequest updateEventDto, @MappingTarget Event event);

    @Mapping(target = "createdOn", source = "event.createdOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "publishedOn", source = "event.publishedOn", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests", defaultValue = "0")
    @Mapping(target = "views", source = "views", defaultValue = "0L")
    EventFullDto toFullDto(Event event, Long views, Long confirmedRequests);

    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "confirmedRequests", source = "confirmedRequests", defaultValue = "0")
    @Mapping(target = "views", source = "views")
    EventShortDto toShortDto(Event event, Long views, Long confirmedRequests);

    @Mapping(target = "eventDate", source = "event.eventDate", dateFormat = "yyyy-MM-dd HH:mm:ss")
    @Mapping(target = "confirmedRequests", ignore = true, defaultValue = "0")
    @Mapping(target = "views", ignore = true, defaultValue = "0")
    EventShortDto toShortDto(Event event);

    default List<EventShortDto> toEventShortDtoListWithSortByViews(List<Event> events,
                                                                   Map<Long, Long> viewStatMap,
                                                                   Map<Long, Long> confirmedRequests) {
        return events.stream()
                .map(event -> toShortDto(event,
                        viewStatMap.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)))
                .sorted((e1, e2) -> e2.getViews().compareTo(e1.getViews()))
                .collect(Collectors.toList());
    }

    default List<EventShortDto> toEventShortDtoList(List<Event> events,
                                                    Map<Long, Long> viewStatMap,
                                                    Map<Long, Long> confirmedRequests) {
        return events.stream()
                .map(event -> toShortDto(event,
                        viewStatMap.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }

    default List<EventFullDto> toEventFullDtoList(List<Event> events,
                                                  Map<Long, Long> viewStatMap,
                                                  Map<Long, Long> confirmedRequests) {
        return events.stream()
                .map(event -> toFullDto(event,
                        viewStatMap.getOrDefault(event.getId(), 0L),
                        confirmedRequests.getOrDefault(event.getId(), 0L)))
                .collect(Collectors.toList());
    }
}
