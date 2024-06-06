package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    @Transactional
    @Override
    public UserDto save(UserCreateDto user) {
        return mapper.toDto(repository.save(mapper.toUser(user)));
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll(List<Long> ids, Pageable pageable) {
        return mapper.toDtoList(repository.findAllByIdIn(ids, pageable));
    }

    @Transactional
    @Override
    public void delete(Long id) {
        repository.deleteById(id);
    }
}