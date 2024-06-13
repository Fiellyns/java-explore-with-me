package ru.practicum.ewm.comment.dto;

import lombok.*;
import ru.practicum.ewm.comment.model.CommentState;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDto {

    private Long id;

    @NonNull
    private Long eventId;

    @NotBlank
    @Size(min = 3, max = 3000)
    private String text;

    @NonNull
    private CommentState state;

    private LocalDateTime created;

}
