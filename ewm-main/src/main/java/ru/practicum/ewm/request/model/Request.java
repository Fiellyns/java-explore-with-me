package ru.practicum.ewm.request.model;

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
@Table(name = "REQUESTS", schema = "PUBLIC",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"EVENT_ID", "USER_ID"})})
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUEST_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User requester;

    @ManyToOne
    @JoinColumn(name = "EVENT_ID")
    private Event event;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private RequestStatus status;

    @Column(name = "CREATED")
    private LocalDateTime created;
}
