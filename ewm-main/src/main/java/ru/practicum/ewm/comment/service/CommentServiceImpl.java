package ru.practicum.ewm.comment.service;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comment.dto.CommentAdminRequestDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.mapper.CommentMapper;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentAdminRequest;
import ru.practicum.ewm.comment.model.CommentState;
import ru.practicum.ewm.comment.model.QComment;
import ru.practicum.ewm.comment.repository.CommentRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotAccessException;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;
import ru.practicum.ewm.request.repository.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    @Transactional
    @Override
    public CommentDto save(long userId, CommentRequestDto createDto) {
        List<Request> requests = requestRepository
                .findByEventIdAndRequesterIdAndStatusIs(createDto.getEventId(), userId, RequestStatus.CONFIRMED);
        if (requests.isEmpty()) {
            throw new NotAccessException("Только участники могут комментировать событие!");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Event event = eventRepository.findById(createDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id:" + createDto.getEventId() + " не найдено"));
        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotAccessException("Невозможно опубликовать комментарий, так как событие еще не опубликовано!");
        }
        if (!createDto.getState().equals(CommentState.PENDING)) {
            throw new NotAccessException("На момент создания состояние должно быть 'ОЖИДАНИЕ'");
        }
        createDto.setCreated(LocalDateTime.now());
        Comment comment = commentRepository.save(
                commentMapper.toModel(createDto, event, user));
        return commentMapper.toCommentDto(comment);
    }

    @Transactional
    @Override
    public CommentDto update(long userId, long commentId, CommentRequestDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id:" + userId + " не найден"));
        Event event = eventRepository.findById(commentDto.getEventId())
                .orElseThrow(() -> new NotFoundException("Событие с id:" + commentDto.getEventId() + " не найдено"));
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id:" + commentId + " не найден"));
        if (comment.getState().equals(CommentState.PUBLISHED)) {
            throw new NotAccessException("Изменять можно только отложенные или отмененные комментарии");
        }
        if (commentDto.getState().equals(CommentState.PUBLISHED)) {
            throw new NotAccessException("Только администратор может установить статус 'ОПУБЛИКОВАНО'");
        }
        Comment updatedComment = commentRepository.save(
                commentMapper.update(commentDto, comment));
        return commentMapper.toCommentDto(updatedComment);
    }

    @Transactional
    @Override
    public void delete(long commentId) {
        commentRepository.deleteById(commentId);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getAll(CommentAdminRequest request, PageRequest pageRequest) {
        QComment comment = QComment.comment;

        List<BooleanExpression> conditions = new ArrayList<>();
        if (request.getUsers() != null && !request.getUsers().isEmpty()) {
            conditions.add(comment.author.id.in(request.getUsers()));
        }
        if (request.getStates() != null && !request.getStates().isEmpty()) {
            conditions.add(comment.state.in(request.getStates()));
        }
        if (request.getEvents() != null && !request.getEvents().isEmpty()) {
            comment.event.id.in(request.getEvents());
        }
        if (request.getRangeStart() != null && request.getRangeEnd() != null) {
            if (request.getRangeStart().isBefore(request.getRangeEnd())) {
                conditions.add(comment.created.between(request.getRangeStart(), request.getRangeEnd()));
            }
        }

        List<Comment> comments;
        if (conditions.isEmpty()) {
            comments = commentRepository.findAll(pageRequest).getContent();
        } else {
            BooleanExpression exp = conditions.stream()
                    .reduce(BooleanExpression::and)
                    .get();
            comments = commentRepository.findAll(exp, pageRequest).getContent();
        }
        return commentMapper.toListCommentDto(comments);
    }

    @Transactional
    @Override
    public CommentDto moderate(long commentId, CommentAdminRequestDto requestDto) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id:" + commentId + " не найден"));
        if (!comment.getState().equals(CommentState.PENDING)) {
            throw new NotAccessException("Невозможно опубликовать событие," +
                    " так как оно находится в неправильном состоянии: " + comment.getState());

        }
        if (requestDto.isStateNeedUpdate()) {
            switch (requestDto.getStateAction()) {
                case REJECT_COMMENT:
                    comment.setState(CommentState.CANCELED);
                    break;
                case PUBLISH_COMMENT:
                    comment.setState(CommentState.PUBLISHED);
                    break;
                default:
                    throw new IllegalArgumentException("Unknown state action: " + requestDto.getStateAction());
            }
        }
        Comment updatedComment = commentRepository.save(
                commentMapper.update(requestDto, comment));

        return commentMapper.toCommentDto(updatedComment);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CommentDto> getAllPublishedByEvent(Long eventId, PageRequest pageRequest) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Событие с id:" + eventId + " не найдено"));
        List<Comment> comments = commentRepository
                .findAllByEventIdAndStateIs(eventId, CommentState.PUBLISHED, pageRequest);
        return commentMapper.toListCommentDto(comments);
    }

    @Transactional(readOnly = true)
    @Override
    public CommentDto getById(long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Комментарий с id:" + commentId + " не найден"));
        return commentMapper.toCommentDto(comment);
    }
}
