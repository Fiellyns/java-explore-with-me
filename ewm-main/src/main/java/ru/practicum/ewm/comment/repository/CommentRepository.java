package ru.practicum.ewm.comment.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.comment.model.Comment;
import ru.practicum.ewm.comment.model.CommentState;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long>, QuerydslPredicateExecutor<Comment> {

    List<Comment> findAllByEventIdAndStateIs(Long eventId, CommentState commentState, PageRequest pageRequest);

    List<Comment> findAllByEventIdInAndStateIs(List<Long> eventIds, CommentState state);
}
