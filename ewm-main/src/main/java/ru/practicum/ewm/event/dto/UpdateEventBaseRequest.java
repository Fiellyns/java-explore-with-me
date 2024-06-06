package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.practicum.ewm.event.model.Location;
import ru.practicum.ewm.event.validation.CheckEventDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@SuperBuilder(builderMethodName = "baseBuilder")
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventBaseRequest {


    @Size(min = 20, max = 2000)
    private String annotation;

    @JsonProperty("category")
    private Long categoryId;

    @Size(min = 20, max = 7000)
    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @CheckEventDate
    private LocalDateTime eventDate;

    private Location location;

    private Boolean paid;

    @Min(0)
    private Integer participantLimit;

    private Boolean requestModeration;

    @Size(min = 3, max = 120)
    private String title;
}