package ru.practicum.ewm.event.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.requestModel.EventAdminRequest;
import ru.practicum.ewm.event.requestModel.EventPublicRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface EventService {

    EventFullDto save(Long userId, NewEventDto eventDto);

    List<EventShortDto> getAllByUserId(Long userId, PageRequest pageRequest);

    EventFullDto getByUserAndEventId(Long userId, Long eventId);

    EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventDto);

    List<RequestDto> getRequestByEventId(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequestsStatus(Long userId,
                                                        Long eventId,
                                                        EventRequestStatusUpdateRequest request);

    List<EventFullDto> getAll(EventAdminRequest request, PageRequest pageRequest);

    EventFullDto moderate(Long eventId, UpdateEventAdminRequest updateEventDto);

    EventFullDto getById(Long id);

    List<EventShortDto> getAll(EventPublicRequest request,
                               String sort,
                               PageRequest pageRequest);
}
