package ru.practicum.ewm.comment.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.comment.dto.CommentAdminRequestDto;
import ru.practicum.ewm.comment.dto.CommentDto;
import ru.practicum.ewm.comment.dto.CommentRequestDto;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.model.User;

import java.util.List;

import static org.mapstruct.MappingConstants.ComponentModel.SPRING;

@Component
@Mapper(componentModel = SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CommentMapper {

    @Mapping(source = "author.name", target = "authorName")
    CommentDto toCommentDto(Comment comment);

    List<CommentDto> toListCommentDto(List<Comment> comments);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "event", source = "event")
    @Mapping(target = "state", source = "createDto.state")
    Comment toModel(CommentRequestDto createDto,
                    Event event,
                    User author);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Comment update(CommentRequestDto commentDto, @MappingTarget Comment comment);

    @Mapping(target = "id", ignore = true)
    Comment update(CommentAdminRequestDto commentDto, @MappingTarget Comment comment);
}
