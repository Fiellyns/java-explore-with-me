package ru.practicum.ewm.request.service;

import ru.practicum.ewm.request.dto.RequestDto;

import java.util.List;

public interface RequestService {

    RequestDto save(long userId, long eventId);

    RequestDto cancelRequest(long userId, long requestId);

    List<RequestDto> getAllById(long userId);

}