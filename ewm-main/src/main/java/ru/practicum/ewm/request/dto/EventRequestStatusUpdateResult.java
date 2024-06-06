package ru.practicum.ewm.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class EventRequestStatusUpdateResult {

    private List<RequestDto> confirmedRequests;

    private List<RequestDto> rejectedRequests;
}
