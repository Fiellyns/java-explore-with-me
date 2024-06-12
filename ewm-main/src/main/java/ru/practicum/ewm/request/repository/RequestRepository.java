package ru.practicum.ewm.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.request.dto.RequestsCountDto;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.model.RequestStatus;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    Integer countByEventIdAndStatusIs(long eventId, RequestStatus status);

    List<Request> findAllByRequesterId(long userId);

    List<Request> findAllByEventId(long eventId);

    List<Request> findAllByIdInAndStatusIs(List<Long> requestIds, RequestStatus status);

    @Query("SELECT new ru.practicum.ewm.request.dto.RequestsCountDto(r.event.id, COUNT(r.id) AS countRequests) " +
            "FROM Request AS r " +
            "WHERE r.status IS (:status) " +
            "AND r.event.id IN (:ids) " +
            "GROUP BY r.event")
    List<RequestsCountDto> findAllConfirmedByEventIdIn(@Param("ids") List<Long> ids,
                                                       @Param("status") RequestStatus status);

    List<Request> findByEventIdAndRequesterIdAndStatusIs(long userId, Long eventId, RequestStatus requestStatus);
}