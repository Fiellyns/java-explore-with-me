package ru.practicum.ewm.comment.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMMENTS", schema = "PUBLIC")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_ID")
    private Long id;

    @Column(name = "TEXT")
    private String text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User author;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    private CommentState state;

    @Column(name = "CREATED_TIME")
    private LocalDateTime created;

}
