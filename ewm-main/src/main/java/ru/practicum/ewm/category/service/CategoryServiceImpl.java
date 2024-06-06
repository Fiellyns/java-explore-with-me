package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryRequestDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotAccessException;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    public final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper mapper;

    @Transactional
    @Override
    public CategoryDto save(CategoryRequestDto dto) {
        return mapper.toDto(categoryRepository.save(mapper.toCategory(dto)));
    }

    @Transactional
    @Override
    public CategoryDto update(Long id, CategoryRequestDto dto) {
        Category updatingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id:" + id + " не найдена"));
        updatingCategory.setName(dto.getName());
        return mapper.toDto(categoryRepository.save(updatingCategory));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (eventRepository.countByCategoryId(id) > 0) {
            throw new NotAccessException("Категория не пустая");
        }
        categoryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public List<CategoryDto> getAll(Pageable pageable) {
        return mapper.toDtoList(categoryRepository.findAll(pageable));
    }

    @Transactional(readOnly = true)
    @Override
    public CategoryDto getById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Категория с id:" + id + " не найдена"));
        return mapper.toDto(category);
    }
}