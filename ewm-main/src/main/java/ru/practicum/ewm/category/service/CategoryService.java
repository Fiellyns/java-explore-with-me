package ru.practicum.ewm.category.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;

import java.util.List;

public interface CategoryService {

    CategoryDto save(CategoryRequestDto dto);

    CategoryDto update(Long id, CategoryRequestDto dto);

    void delete(Long id);

    List<CategoryDto> getAll(Pageable pageable);

    CategoryDto getById(long id);
}