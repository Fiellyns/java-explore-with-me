package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationDto {
    @Builder.Default
    private List<Long> events = new ArrayList<>();
    private Boolean pinned;
    @Size(min = 1, max = 50)
    private String title;
}
