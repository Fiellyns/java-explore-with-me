package ru.practicum.ewm.compilation.model;

import lombok.*;
import ru.practicum.ewm.event.model.Event;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "COMPILATIONS", schema = "PUBLIC")
public class Compilation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMPILATION_ID")
    private Long id;
    @Column(name = "TITLE")
    private String title;
    @Column(name = "PINNED")
    private Boolean pinned;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "COMPILATIONS_EVENTS",
            joinColumns = @JoinColumn(name = "COMPILATION_ID"),
            inverseJoinColumns = @JoinColumn(name = "EVENT_ID"))
    @Builder.Default
    private List<Event> events = new ArrayList<>();
}
