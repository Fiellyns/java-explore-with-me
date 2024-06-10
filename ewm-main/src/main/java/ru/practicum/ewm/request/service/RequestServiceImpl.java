package ru.practicum.ewm.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final RequestMapper requestMapper;
    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public RequestDto save(long userId, long eventId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new IllegalArgumentException("Невозможно добавить запрос на участие в вашем мероприятии!");
        }
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new IllegalArgumentException("Событие еще не опубликовано!");
        }
        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit().equals(
                        requestRepository.countByEventIdAndStatusIs(eventId, RequestStatus.CONFIRMED))) {
            throw new IllegalArgumentException("Достигнут лимит заявок на участие!");
        }
        Request request = Request.builder()
                .event(event)
                .requester(user)
                .status(RequestStatus.PENDING)
                .created(LocalDateTime.now().withNano(0))
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request.setStatus(RequestStatus.CONFIRMED);
        }

        return requestMapper.toDto(
                requestRepository.save(request));
    }

    @Transactional
    @Override
    public RequestDto cancelRequest(long userId, long requestId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос с id:" + requestId + " не найден"));
        request.setStatus(RequestStatus.CANCELED);
        return requestMapper.toDto(
                requestRepository.save(request));
    }

    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getAllById(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }
        return requestMapper.toDtoList(requestRepository.findAllByRequesterId(userId));
    }
}