package ru.practicum.ewm.category.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@Validated
@Slf4j
@RequestMapping("/categories")
public class CategoryPublicController {

    private final CategoryService service;

    @Autowired
    public CategoryPublicController(CategoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<CategoryDto> getAll(
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил GET-запрос в /categories");
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.unsorted());
        List<CategoryDto> categories = service.getAll(pageRequest);
        log.info("GET-запрос /categories был обработан: {}", categories);
        return categories;
    }

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable Long catId) {
        log.info("Поступил GET-запрос в /categories/{}", catId);
        CategoryDto categoryDto = service.getById(catId);
        log.info("GET-запрос /categories/{} был обработан: {}", catId, categoryDto);
        return categoryDto;
    }

}