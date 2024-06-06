package ru.practicum.ewm.event.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "LOCATIONS")
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "LOCATION_ID")
    private Long id;

    @NotNull
    @JsonProperty("lat")
    @Column(name = "LATITUDE")
    private Double latitude;

    @NotNull
    @JsonProperty("lon")
    @Column(name = "LONGITUDE")
    private Double longitude;
}
