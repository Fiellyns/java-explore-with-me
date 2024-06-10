package ru.practicum.ewm.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.request.model.RequestStatus;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@Builder
public class EventRequestStatusUpdateRequest {

    @NotEmpty
    private List<Long> requestIds;

    private RequestStatus status;
}
