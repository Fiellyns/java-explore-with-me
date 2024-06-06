package ru.practicum.ewm.compilation.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/compilations")
public class CompilationAdminController {

    @Autowired
    private CompilationService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public CompilationDto save(@RequestBody @Valid NewCompilationDto dto) {
        log.info("Поступил POST-запрос в /admin/compilations: {}", dto);
        CompilationDto createdCompilation = service.save(dto);
        log.info("POST-запрос /admin/compilations был обработан: {}", createdCompilation);
        return createdCompilation;
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Long compId,
                                 @RequestBody @Valid UpdateCompilationDto dto) {
        log.info("Поступил PATCH-запрос в /admin/compilations/{}: {}", compId, dto);
        CompilationDto updatedCompilation = service.update(compId, dto);
        log.info("PATCH-запрос /admin/compilations/{} был обработан: {}", compId, updatedCompilation);
        return updatedCompilation;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{compId}")
    public void delete(@PathVariable Long compId) {
        log.info("Поступил DELETE-запрос в /admin/compilations/{}", compId);
        service.delete(compId);
        log.info("DELETE-запрос /admin/compilations/{} был обработан", compId);
    }
}