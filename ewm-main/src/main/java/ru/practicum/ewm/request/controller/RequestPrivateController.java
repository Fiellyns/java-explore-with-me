package ru.practicum.ewm.request.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.service.RequestService;

import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/users/{userId}/requests")
public class RequestPrivateController {

    private final RequestService service;

    @Autowired
    public RequestPrivateController(RequestService service) {
        this.service = service;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public RequestDto save(@PathVariable Long userId,
                           @RequestParam Long eventId) {
        log.info("Поступил POST-запрос в /users/{}/requests событие:{}", userId, eventId);
        RequestDto request = service.save(userId, eventId);
        log.info("POST-запрос /users/{}/requests событие:{} был обработан: {}", userId, eventId, request);
        return request;
    }

    @GetMapping
    public List<RequestDto> getAllByUser(@PathVariable Long userId) {
        log.info("Поступил GET-запрос в /users/userId={}/requests:", userId);
        List<RequestDto> requests = service.getAllById(userId);
        log.info("POST-запрос /users/{}/requests был обработан: {}", userId, requests);
        return requests;
    }

    @PatchMapping("/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("Поступил PATCH-запрос в /users/{}/requests/{}/cancel:", userId, requestId);
        RequestDto request = service.cancelRequest(userId, requestId);
        log.info("PATCH-запрос /users/{}/requests/{}/cancel был обработан: {}", userId, requestId, request);
        return request;
    }
}