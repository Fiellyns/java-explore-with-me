package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.lang.Nullable;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateEventUserRequest extends UpdateEventBaseRequest {

    private @Nullable StateAction stateAction;

    public boolean isStateNeedUpdate() {
        return stateAction != null;
    }

    public enum StateAction {
        SEND_TO_REVIEW,
        CANCEL_REVIEW
    }
}
