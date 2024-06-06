package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.user.dto.UserCreateDto;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.dto.UserShortDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING)
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toUser(UserCreateDto userCreateDto);

    UserDto toDto(User user);

    UserShortDto toShortDto(User user);

    List<UserDto> toDtoList(List<User> users);
}
