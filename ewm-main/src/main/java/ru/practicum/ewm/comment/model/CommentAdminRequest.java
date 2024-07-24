package ru.practicum.ewm.comment.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentAdminRequest {

    private List<Long> users;

    private List<CommentState> states;

    private List<Long> events;

    private LocalDateTime rangeStart;

    private LocalDateTime rangeEnd;

}
