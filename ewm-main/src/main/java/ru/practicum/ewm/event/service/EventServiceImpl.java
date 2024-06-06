package ru.practicum.ewm.event.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatisticClient;
import ru.practicum.ewm.ViewStatDto;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.controller.SortQuery;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.event.requestModel.EventAdminRequest;
import ru.practicum.ewm.event.requestModel.EventPublicRequest;
import ru.practicum.ewm.exception.NotAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.request.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestsCountDto;
import ru.practicum.ewm.request.mapper.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.practicum.ewm.event.model.EventState.*;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
    private final EventMapper eventMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final RequestMapper requestMapper;
    private final StatisticClient client;
    private final EventRepository eventRepository;

    @Transactional
    @Override
    public EventFullDto save(Long userId, NewEventDto eventDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        long categoryId = eventDto.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Категория с id:" + userId + " не найдена"));
        Event event = eventRepository.save(
                eventMapper.toEvent(eventDto, user, category, EventState.PENDING));
        return eventMapper.toFullDto(event, null, null);
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAllByUserId(Long userId, PageRequest pageRequest) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageRequest);
        if (events.isEmpty()) {
            return Collections.emptyList();
        }

        Map<Long, Long> viewStatMap = getEventViews(events);

        Map<Long, Long> confirmedRequestMap = getConfirmedRequests(events);

        return eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap, confirmedRequestMap);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getByUserAndEventId(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено"));

        Map<Long, Long> viewStatMap = getEventViews(List.of(event));

        Map<Long, Long> confirmedRequestMap = getConfirmedRequests(List.of(event));

        return eventMapper.toFullDto(event,
                viewStatMap.getOrDefault(eventId, 0L),
                confirmedRequestMap.getOrDefault(eventId, 0L));
    }

    @Transactional
    @Override
    public EventFullDto update(Long userId, Long eventId, UpdateEventUserRequest updateEventDto) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + userId + " не найдено"));
        if (event.getState().equals(PUBLISHED)) {
            throw new NotAccessException("Изменить можно только запланированные или отмененные события!");
        }
        if (updateEventDto.isStateNeedUpdate()) {
            switch (updateEventDto.getStateAction()) {
                case CANCEL_REVIEW:
                    event.setState(CANCELED);
                    break;
                case SEND_TO_REVIEW:
                    event.setState(PENDING);
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown state action: " + updateEventDto.getStateAction());
            }
        }

        Event updatedEvent = eventRepository.save(
                eventMapper.update(updateEventDto, event));

        return eventMapper.toFullDto(updatedEvent, null, null);

    }

    @Transactional(readOnly = true)
    @Override
    public List<EventFullDto> getAll(EventAdminRequest request, PageRequest pageRequest) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();
        if (request.getUsers() != null && !request.getUsers().isEmpty()) {
            conditions.add(event.initiator.id.in(request.getUsers()));
        }
        if (request.getStates() != null && !request.getStates().isEmpty()) {
            conditions.add(event.state.in(request.getStates()));
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            event.category.id.in(request.getCategories());
        }
        if (request.getRangeStart() != null && request.getRangeEnd() != null) {
            if (request.getRangeStart().isAfter(request.getRangeEnd())) {
                conditions.add(event.eventDate.between(request.getRangeStart(), request.getRangeEnd()));
            }
        }

        List<Event> events;

        if (conditions.isEmpty()) {
            events = eventRepository.findAll(pageRequest).getContent();
        } else {
            BooleanExpression exp = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
            events = eventRepository.findAll(exp, pageRequest).getContent();
        }

        Map<Long, Long> viewStatMap = getEventViews(events);

        Map<Long, Long> confirmedRequestMap = getConfirmedRequests(events);

        return eventMapper.toEventFullDtoList(events, viewStatMap, confirmedRequestMap);
    }

    @Transactional
    @Override
    public EventFullDto moderate(Long eventId, UpdateEventAdminRequest updateEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено"));
        if (!event.getState().equals(EventState.PENDING)) {
            throw new NotAccessException("Невозможно опубликовать событие," +
                    " так как оно находится в неправильном состоянии: " + event.getState());
        }
        if (event.getEventDate().plusHours(1)
                .isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("EventDate должен начаться не позднее, чем через 1 час");
        }

        if (updateEventDto.isStateNeedUpdate()) {
            switch (updateEventDto.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(PUBLISHED);
                    event.setPublishedOn(
                            LocalDateTime.now().withNano(0));
                    break;
                default:
                    throw new IllegalArgumentException(
                            "Unknown state action: " + updateEventDto.getStateAction());
            }
        }

        Event moderatedEvent = eventRepository.save(
                eventMapper.update(updateEventDto, event));

        return eventMapper.toFullDto(moderatedEvent, null, null);
    }

    @Transactional(readOnly = true)
    @Override
    public EventFullDto getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + id + " не найдено"));
        if (!event.getState().equals(PUBLISHED)) {
            throw new NotFoundException("Событие id=" + id + " еще не опубликовано");
        }

        Map<Long, Long> viewStatMap = getEventViews(List.of(event));

        Map<Long, Long> confirmedRequestMap = getConfirmedRequests(List.of(event));

        return eventMapper.toFullDto(event,
                viewStatMap.getOrDefault(id, 0L),
                confirmedRequestMap.getOrDefault(id, 0L));
    }

    @Transactional(readOnly = true)
    @Override
    public List<EventShortDto> getAll(EventPublicRequest request,
                                      String sort,
                                      PageRequest pageRequest) {
        QEvent event = QEvent.event;

        List<BooleanExpression> conditions = new ArrayList<>();
        conditions.add(event.state.eq(PUBLISHED));

        if (request.getText() != null) {
            conditions.add(event.annotation.containsIgnoreCase(request.getText())
                    .or(event.description.containsIgnoreCase(request.getText())));
        }
        if (request.getCategories() != null && !request.getCategories().isEmpty()) {
            conditions.add(event.category.id.in(request.getCategories()));
        }
        if (request.getPaid() != null) {
            conditions.add(event.paid.eq(request.getPaid()));
        }
        if (request.getRangeStart().isAfter(request.getRangeEnd())) {
            conditions.add(event.eventDate.between(request.getRangeStart(), request.getRangeEnd()));
        }

        BooleanExpression exp = conditions.stream()
                .filter(Objects::nonNull)
                .reduce(BooleanExpression::and)
                .get();

        List<Event> events = eventRepository.findAll(exp, pageRequest).getContent();

        Map<Long, Long> viewStatMap = getEventViews(events);

        Map<Long, Long> confirmedRequestMap = getConfirmedRequests(events);

        if (request.getOnlyAvailable()) {
            events = events.stream()
                    .filter(e ->
                            e.getParticipantLimit() > confirmedRequestMap.getOrDefault(e.getId(), 0L))
                    .collect(Collectors.toList());
        }

        if (sort != null && sort.equals(SortQuery.VIEWS.toString())) {
            return eventMapper.toEventShortDtoListWithSortByViews(events, viewStatMap, confirmedRequestMap);
        }

        return eventMapper.toEventShortDtoList(events, viewStatMap, confirmedRequestMap);
    }


    @Transactional(readOnly = true)
    @Override
    public List<RequestDto> getRequestByEventId(Long userId, Long eventId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException("Событие с id:" + eventId + " не найдено");
        }
        return requestMapper.toDtoList(requestRepository.findAllByEventId(eventId));
    }

    @Transactional
    @Override
    public EventRequestStatusUpdateResult updateRequestsStatus(
            Long userId,
            Long eventId,
            EventRequestStatusUpdateRequest request) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id:" + userId + " не найден");
        }
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено"));

        Map<Long, Long> confirmedRequestMap = getConfirmedRequests(List.of(event));
        long participantLimit = event.getParticipantLimit();
        long currentCountParticipant = confirmedRequestMap.getOrDefault(eventId, 0L);

        if (participantLimit == currentCountParticipant) {
            throw new IllegalArgumentException("Достигнут лимит заявок на участие!");
        }

        List<Request> updatingRequests = requestRepository.findAllByIdInAndStatusIs(
                request.getRequestIds(), RequestStatus.PENDING);
        if (updatingRequests.isEmpty() || updatingRequests.size() < request.getRequestIds().size()) {
            throw new IllegalArgumentException(
                    "Изменить можно только запрос со статусом: " + RequestStatus.PENDING);
        }
        for (Request r : updatingRequests) {
            if (participantLimit == currentCountParticipant) {
                r.setStatus(RequestStatus.REJECTED);
            }
            if (request.getStatus().equals(RequestStatus.CONFIRMED)) {
                r.setStatus(RequestStatus.CONFIRMED);
                currentCountParticipant++;
            } else {
                r.setStatus(RequestStatus.REJECTED);
            }
        }

        return requestMapper.toStatusUpdateResult(
                requestRepository.saveAll(updatingRequests));
    }

    private Map<Long, Long> getEventViews(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Long> eventUriAndIdMap = events.stream()
                .map(Event::getId)
                .collect(Collectors.toMap(id -> "/events/" + id, Function.identity()));

        List<ViewStatDto> stats = client.getStatistics(
                LocalDateTime.now().minusYears(10).format(FORMATTER),
                LocalDateTime.now().withNano(0).format(FORMATTER),
                List.copyOf(eventUriAndIdMap.keySet()),
                Boolean.TRUE);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> eventUriAndIdMap.get(stat.getUri()), ViewStatDto::getHits)
                );
    }

    private Map<Long, Long> getConfirmedRequests(List<Event> events) {
        if (events.isEmpty()) {
            return Collections.emptyMap();
        }
        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());
        List<RequestsCountDto> confirmedRequests = requestRepository
                .findAllConfirmedByEventIdIn(eventIds, RequestStatus.CONFIRMED);

        return confirmedRequests.stream()
                .collect(Collectors.toMap(
                        RequestsCountDto::getEventId, RequestsCountDto::getCountRequests));

    }
}
