package ru.practicum.ewm.user.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto save(UserCreateDto user);

    List<UserDto> getAll(List<Long> ids, Pageable pageable);

    void delete(Long id);
}
