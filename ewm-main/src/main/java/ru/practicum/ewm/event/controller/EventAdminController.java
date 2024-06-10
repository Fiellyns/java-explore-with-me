package ru.practicum.ewm.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.requestModel.EventAdminRequest;
import ru.practicum.ewm.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Validated
@Slf4j
@RequestMapping("/admin/events")
public class EventAdminController {

    private final EventService service;

    @Autowired
    public EventAdminController(EventService service) {
        this.service = service;
    }

    @GetMapping
    public List<EventFullDto> getAll(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил GET-запрос в /admin/events: " +
                        "users={}, states={}, categories={}, rangeStart={}, rangeEnd={}",
                users, states, categories, rangeStart, rangeEnd);
        List<EventState> statesList;
        if (states != null) {
            statesList = states.stream()
                    .map(EventState::from)
                    .collect(Collectors.toList());
        } else {
            statesList = null;
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<EventFullDto> fullDtoList = service.getAll(
                new EventAdminRequest(users, statesList, categories, rangeStart, rangeEnd),
                pageRequest);
        log.info("GET-запрос /admin/events был обработан: {}", fullDtoList);
        return fullDtoList;
    }

    @PatchMapping("/{eventId}")
    public EventFullDto moderate(@PathVariable Long eventId,
                                 @RequestBody @Valid UpdateEventAdminRequest updateEventDto) {
        log.info("Поступил PATCH-запрос в /admin/events/{}: событие: {}", eventId, updateEventDto);
        EventFullDto moderatedEvent = service.moderate(eventId, updateEventDto);
        log.info("PATCH-запрос /admin/events/{} был обработан: {}", eventId, moderatedEvent);
        return moderatedEvent;
    }

}