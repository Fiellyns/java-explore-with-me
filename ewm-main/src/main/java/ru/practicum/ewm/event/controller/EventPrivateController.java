package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/users/{userId}/events")
public class EventPrivateController {

    private final EventService service;

    @Autowired
    public EventPrivateController(EventService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public EventFullDto save(@PathVariable Long userId,
                             @RequestBody @Valid NewEventDto eventDto) {
        log.info("Поступил POST-запрос в /users/{}/events: {}", userId, eventDto);
        EventFullDto savedEvent = service.save(userId, eventDto);
        log.info("POST-запрос /users/{}/events был обработан: {}", userId, savedEvent);
        return savedEvent;
    }

    @GetMapping
    public List<EventShortDto> getAllByUser(
            @PathVariable Long userId,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил GET-запрос в /users/{}/events", userId);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<EventShortDto> shortDtoList = service.getAllByUserId(userId, pageRequest);
        log.info("GET-запрос /users/{}/events был обработан: {}", userId, shortDtoList);
        return shortDtoList;
    }

    @GetMapping("/{eventId}")
    public EventFullDto getByUserAndEventId(
            @PathVariable Long userId,
            @PathVariable Long eventId) {
        log.info("Поступил GET-запрос в /users/{}/events/{}", userId, eventId);
        EventFullDto fullDto = service.getByUserAndEventId(userId, eventId);
        log.info("GET-запрос /users/{}/events/{} был обработан: {}", userId, eventId, fullDto);
        return fullDto;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto update(@PathVariable Long userId,
                               @PathVariable Long eventId,
                               @RequestBody @Valid UpdateEventUserRequest updateEventDto) {
        log.info("Поступил PATCH-запрос в /users/{}/events/{}: {}", userId, eventId, updateEventDto);
        EventFullDto updatedEvent = service.update(userId, eventId, updateEventDto);
        log.info("PATCH-запрос /users/{}/events/{} был обработан: {}", userId, eventId, updatedEvent);
        return updatedEvent;
    }

    @GetMapping("/{eventId}/requests")
    public List<RequestDto> getRequestsByEvent(@PathVariable Long userId,
                                               @PathVariable Long eventId) {
        log.info("Поступил GET-запрос в /users/{}/events/{}/requests", userId, eventId);
        List<RequestDto> requests = service.getRequestByEventId(userId, eventId);
        log.info("GET-запрос /users/{}/events/{}/requests был обработан: {}", userId, eventId, requests);
        return requests;
    }

    @PatchMapping("/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestStatus(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        log.info("Поступил PATCH-запрос в /users/{}/events/{}/requests: событие: {}", userId, eventId, request);
        EventRequestStatusUpdateResult updatedRequests = service.updateRequestsStatus(userId, eventId, request);
        log.info("PATCH-запрос /users/{}/events/{}/requests был обработан: {}", userId, eventId, updatedRequests);
        return updatedRequests;
    }

}