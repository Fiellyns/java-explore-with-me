package ru.practicum.ewm.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/compilations")
public class CompilationPublicController {

    private static final int DEFAULT_SIZE = 10;

    @Autowired
    private CompilationService service;

    @GetMapping
    public List<CompilationDto> getAll(
            @RequestParam(value = "pinned", defaultValue = "true", required = false) Boolean pinned,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил GET-запрос в /compilations: pinned={}, from={}, size={}", pinned, from, size);
        if (size < DEFAULT_SIZE) {
            size = DEFAULT_SIZE;
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<CompilationDto> compilations = service.getAll(pinned, pageRequest);
        log.info("GET-запрос /compilations был обработан: {}", compilations);
        return compilations;
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        log.info("Поступил GET-запрос в /compilations/{}", compId);
        CompilationDto compilation = service.getById(compId);
        log.info("GET-запрос /compilations/{} был обработан: {}", compId, compilation);
        return compilation;
    }
}