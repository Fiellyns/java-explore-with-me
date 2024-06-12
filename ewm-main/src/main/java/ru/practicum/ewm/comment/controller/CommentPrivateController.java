package ru.practicum.ewm.comment.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.service.CommentService;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping("/users/{userId}/events/comments")
@RequiredArgsConstructor
public class CommentPrivateController {

    private final CommentService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CommentDto save(@PathVariable long userId,
                           @RequestBody @Valid CommentRequestDto createDto) {
        log.info("Поступил POST-запрос в /users/{}/events/comments: {}", userId, createDto);
        CommentDto savedComment = service.save(userId, createDto);
        log.info("POST-запрос /users/{}/events/comments был обработан: {}", userId, savedComment);
        return savedComment;
    }

    @PatchMapping("/{commentId}")
    public CommentDto update(@PathVariable long userId,
                             @PathVariable long commentId,
                             @RequestBody @Valid CommentRequestDto commentDto) {
        log.info("Поступил PATCH-запрос в /users/{}/events/comments/{}: {}", userId, commentId, commentDto);
        CommentDto updatedComment = service.update(userId, commentId, commentDto);
        log.info("PATCH-запрос /users/{}/events/comments/{} был обработан: {}", userId, commentId, updatedComment);
        return updatedComment;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{commentId}")
    public void delete(@PathVariable long userId,
                       @PathVariable long commentId) {
        log.info("Поступил DELETE-запрос в /users/{}/events/comments/{}", userId, commentId);
        service.delete(commentId);
        log.info("DELETE-запрос /users/{}/events/comments/{} был обработан", userId, commentId);
    }
}