package ru.practicum.ewm.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.StatisticClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.requestModel.EventPublicRequest;
import ru.practicum.ewm.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static ru.practicum.ewm.event.controller.SortQuery.EVENT_DATE;

@RestController
@Validated
@Slf4j
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventPublicController {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    @Autowired
    private final EventService service;
    @Autowired
    private StatisticClient client;

    @GetMapping
    public List<EventShortDto> getAll(
            @RequestParam(required = false) @Size(min = 2) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(value = "rangeStart", defaultValue = "#{T(java.time.LocalDateTime).now()}", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeStart,
            @RequestParam(value = "rangeEnd", defaultValue = "#{T(java.time.LocalDateTime).MAX}", required = false)
            @DateTimeFormat(pattern = DATE_FORMAT) LocalDateTime rangeEnd,
            @RequestParam(value = "onlyAvailable", defaultValue = "false", required = false) Boolean onlyAvailable,
            @RequestParam(required = false) String sort,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size,
            HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now().withNano(0);
        log.info("Поступил GET-запрос в /events: " +
                        "text={}, categories={}, paid={}, rangeStart={}, rangeEnd={}, onlyAvailable={}, sort={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        if (sort != null && sort.equals(EVENT_DATE.toString())) {
            pageRequest = pageRequest.withSort(Sort.Direction.ASC, "eventDate");
        }
        List<EventShortDto> shortDtoList = service.getAll(
                new EventPublicRequest(text, categories, paid, rangeStart, rangeEnd, onlyAvailable),
                sort,
                pageRequest);
        log.info("GET-запрос /admin/events был обработан: {}", shortDtoList);
        EndpointHitDto createDto = EndpointHitDto.builder()
                .app("ewm-main.service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(now)
                .build();
        client.postHit(createDto);
        log.info("Запрос данных отправлен на statistic-server: {}", createDto);
        return shortDtoList;
    }

    @GetMapping("/{id}")
    public EventFullDto getById(@PathVariable Long id,
                                HttpServletRequest request) {
        log.info("Поступил GET-запрос в /events/{} ip={}", id, request.getRemoteAddr());
        String ip = request.getRemoteAddr();
        String path = request.getRequestURI();
        EventFullDto fullDto = service.getById(id);
        log.info("GET-запрос /events/{} был обработан: {}", id, fullDto);
        EndpointHitDto createDto = EndpointHitDto.builder()
                .app("ewm-main.service")
                .ip(request.getRemoteAddr())
                .uri(request.getRequestURI())
                .timestamp(LocalDateTime.parse(fullDto.getCreatedOn(), FORMATTER))
                .build();
        client.postHit(createDto);
        log.info("Запрос данных отправлен на statistic-server: {}", createDto);
        return fullDto;
    }
}