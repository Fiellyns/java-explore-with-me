package ru.practicum.ewm.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.user.dto.UserShortDto;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventFullDto {

    private Long id;

    private String title;

    private String annotation;

    private CategoryDto category;

    private Boolean paid;

    private String eventDate;

    private UserShortDto initiator;

    private String description;

    private Integer participantLimit;

    private EventState state;

    private String createdOn;

    private Location location;

    private Boolean requestModeration;

    private Integer confirmedRequests;

    private String publishedOn;

    private Long views;
}
