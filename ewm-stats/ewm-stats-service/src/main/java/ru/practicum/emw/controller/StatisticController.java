package ru.practicum.emw.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.emw.service.StatisticService;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStatDto;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@Validated
public class StatisticController {
    private final StatisticService statisticService;

    @Autowired
    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/hit")
    public void save(@RequestBody @Valid EndpointHitDto statData) {
        log.info("Поступил POST-запрос в /hit");
        EndpointHitDto endpointHitDto = statisticService.save(statData);
        log.info("POST-запрос /hit был обработан: {}", endpointHitDto);
    }

    @GetMapping("/stats")
    public List<ViewStatDto> getHits(@RequestParam("start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                     @RequestParam("end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                     @RequestParam(required = false) List<String> uris,
                                     @RequestParam(required = false, defaultValue = "false") Boolean unique) {
        log.info("Поступил GET-запрос в /stats: start={}, end={}, uris={}, unique={}", start, end, uris, unique);
        List<ViewStatDto> statDataList = statisticService.getHits(
                start, end, uris, unique);
        log.info("GET-запрос /stats был обработан: {}", statDataList);
        return statDataList;
    }

}
