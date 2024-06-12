package ru.practicum.ewm.comment.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.ewm.comment.dto.CommentAdminRequestDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.model.CommentAdminRequest;

import java.util.List;

public interface CommentService {

    CommentDto save(long userId, CommentRequestDto createDto);

    CommentDto update(long userId, long commentId, CommentRequestDto commentDto);

    void delete(long commentId);

    List<CommentDto> getAll(CommentAdminRequest request,
                            PageRequest pageRequest);

    CommentDto moderate(long commentId, CommentAdminRequestDto requestDto);

    List<CommentDto> getAllPublishedByEvent(Long eventId, PageRequest pageRequest);

    CommentDto getById(long commentId);

}
