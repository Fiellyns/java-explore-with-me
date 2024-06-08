package ru.practicum.ewm.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class UserAdminController {
    private final UserService service;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto save(@RequestBody @Valid UserCreateDto userCreateDto) {
        log.info("Поступил POST-запрос в /admin/users");
        UserDto createdUser = service.save(userCreateDto);
        log.info("POST-запрос /admin/users был обработан: {}", createdUser);
        return createdUser;
    }

    @GetMapping
    public List<UserDto> getAll(
            @RequestParam(required = false) List<Long> ids,
            @PositiveOrZero @RequestParam(value = "from", defaultValue = "0", required = false) Integer from,
            @Positive @RequestParam(value = "size", defaultValue = "10", required = false) Integer size) {
        log.info("Поступил GET-запрос в /admin/users: ids={}, from={}, size={}", ids, from, size);
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size);
        List<UserDto> users = service.getAll(ids, pageRequest);
        log.info("GET-запрос /admin/users был обработан: {}", users);
        return users;
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Поступил DELETE-запрос в /admin/users/{}", userId);
        service.delete(userId);
        log.info("DELETE-запрос /admin/users/{} был обработан", userId);
    }
}
