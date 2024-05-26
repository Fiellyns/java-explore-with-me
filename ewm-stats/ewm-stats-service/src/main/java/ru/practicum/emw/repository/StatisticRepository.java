package ru.practicum.emw.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.emw.model.StatisticData;
import ru.practicum.ewm.ViewStatDto;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<StatisticData, Long> {
    @Query("SELECT new ru.practicum.ewm.ViewStatDto(s.app, s.uri, COUNT(s.ip) AS hits) " +
            "FROM StatisticData AS s " +
            "WHERE s.timestamp BETWEEN (:start) AND (:end) " +
            "AND ((:uris) IS NULL OR s.uri IN (:uris)) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStatDto> findAllByTimeBetween(@Param("start") LocalDateTime start,
                                           @Param("end") LocalDateTime end,
                                           @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ewm.ViewStatDto(s.app, s.uri, COUNT(DISTINCT s.ip) AS hits) " +
            "FROM StatisticData AS s " +
            "WHERE s.timestamp BETWEEN (:start) AND (:end) " +
            "AND ((:uris) IS NULL OR s.uri IN (:uris)) " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY hits DESC")
    List<ViewStatDto> findAllUniqueHitByTimeBetween(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end,
                                                    @Param("uris") List<String> uris);
}
