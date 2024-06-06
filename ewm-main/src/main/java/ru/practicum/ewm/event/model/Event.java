package ru.practicum.ewm.event.model;

import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "EVENTS", schema = "PUBLIC")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "EVENT_ID")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "USER_ID")
    private User initiator;

    @Column(name = "ANNOTATION", length = 2000)
    private String annotation;

    @ManyToOne
    @JoinColumn(name = "CATEGORY_ID")
    private Category category;
    @Column(name = "DESCRIPTION", length = 7000)
    private String description;

    @Column(name = "EVENT_DATE")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime eventDate;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "LOCATION_ID")
    private Location location;

    @Column(name = "PAID")
    private Boolean paid;

    @Column(name = "PARTICIPANT_LIMIT")
    private Integer participantLimit;

    @Column(name = "REQUEST_MODERATION")
    private Boolean requestModeration;

    @Column(name = "TITLE")
    private String title;

    @Column(name = "CREATED_ON")
    private LocalDateTime createdOn;

    @Column(name = "PUBLISHED_ON")
    private LocalDateTime publishedOn;

    @Column(name = "STATE")
    @Enumerated(EnumType.STRING)
    private EventState state;

    @Transient
    private Long confirmedRequest;
}
