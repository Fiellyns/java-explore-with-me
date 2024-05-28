package ru.practicum.emw;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.emw.controller.StatisticController;
import ru.practicum.emw.service.StatisticService;
import ru.practicum.ewm.EndpointHitDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = StatisticController.class)
class StatisticControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private StatisticService service;

    @Autowired
    private MockMvc mvc;

    private EndpointHitDto dto;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @BeforeEach
    void setUp() {
        String date = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        dto = EndpointHitDto.builder()
                .app("app")
                .uri("uri")
                .ip("ip")
                .timestamp(LocalDateTime.parse(date, DATE_TIME_FORMATTER))
                .build();
    }


    @Test
    void save_whenDataIsValid_thenReturnIsCreatedAndResponseDto() throws Exception {

        mvc.perform(post("/hit")
                        .content(String.valueOf(mapper.writeValueAsString(dto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(service).save(any(EndpointHitDto.class));
    }

    @Test
    void save_whenDataIsNotValid_thenReturnIsBadRequest() throws Exception {
        dto.setApp(null);

        mvc.perform(post("/hit")
                        .content(String.valueOf(mapper.writeValueAsString(dto)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verify(service, never()).save(any(EndpointHitDto.class));
    }

    @Test
    void getHits_whenRequestIsValid_thenStatusIsOk() throws Exception {
        when(service.getHits(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList(),
                anyBoolean()))
                .thenReturn(new ArrayList<>());

        mvc.perform(get("/stats")
                        .param("start", "2020-05-05 00:00:00")
                        .param("end", "2035-05-05 00:00:00")
                        .param("uris", "/events")
                        .param("unique", "false")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(service).getHits(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList(),
                anyBoolean());
    }
}