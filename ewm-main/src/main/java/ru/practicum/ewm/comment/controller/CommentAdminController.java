package ru.practicum.ewm.comment.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentAdminRequestDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.model.CommentAdminRequest;
import ru.practicum.ewm.comment.model.CommentState;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
public class CommentAdminController {

    private final CommentService service;

    @GetMapping
    public List<CommentDto> getAll(
            @RequestParam(required = false) List<Long> users,
            @RequestParam(required = false) List<String> states,
            @RequestParam(required = false) List<Long> events,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил GET-запрос в /admin/comments: " +
                        "users={}, states={}, events={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, events, rangeStart, rangeEnd, from, size);
        List<CommentState> statesList;
        if (states != null) {
            statesList = states.stream()
                    .map(CommentState::from)
                    .collect(Collectors.toList());
        } else {
            statesList = null;
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<CommentDto> comments = service.getAll(new CommentAdminRequest(users, statesList,
                        events, rangeStart, rangeEnd),
                pageRequest);
        log.info("GET-запрос /admin/comments был обработан: {}", comments);
        return comments;
    }

    @PatchMapping("/{commentId}")
    public CommentDto moderate(@PathVariable long commentId,
                               @RequestBody CommentAdminRequestDto requestDto) {
        log.info("Поступил PATCH-запрос в /admin/comments/{}: комментарий: {}", commentId, requestDto);
        CommentDto moderatedComment = service.moderate(commentId, requestDto);
        log.info("PATCH-запрос /admin/comments/{} был обработан: {}", commentId, moderatedComment);
        return moderatedComment;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable long commentId) {
        log.info("Поступил DELETE-запрос в /admin/comments/{}", commentId);
        service.deleteByAdmin(commentId);
        log.info("DELETE-запрос /admin/comments/{} был обработан", commentId);
    }
}