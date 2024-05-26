package ru.practicum.emw;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.ewm.StatisticCreateDataDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class StatisticCreateDataDtoTest {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    @Autowired
    private JacksonTester<StatisticCreateDataDto> json;

    @Test
    void testStatDataCreateDto() throws IOException {
        String date = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        StatisticCreateDataDto dto = StatisticCreateDataDto.builder()
                .app("ewm-main-service")
                .uri("/events/1")
                .ip("64.233.163.101")
                .timestamp(LocalDateTime.parse(date, DATE_TIME_FORMATTER))
                .build();

        JsonContent<StatisticCreateDataDto> result = json.write(dto);

        assertThat(result).extractingJsonPathStringValue("$.app").isEqualTo(dto.getApp());
        assertThat(result).extractingJsonPathStringValue("$.uri").isEqualTo(dto.getUri());
        assertThat(result).extractingJsonPathStringValue("$.ip").isEqualTo(dto.getIp());
        assertThat(result).extractingJsonPathStringValue("$.timestamp").isEqualTo(date);
    }
}
