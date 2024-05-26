package ru.practicum.emw;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.emw.mapper.StatisticMapper;
import ru.practicum.emw.model.StatisticData;
import ru.practicum.emw.repository.StatisticRepository;
import ru.practicum.emw.service.StatisticServiceImpl;
import ru.practicum.ewm.StatisticCreateDataDto;
import ru.practicum.ewm.ViewStatDto;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticServiceImplTest {

    @Mock
    private StatisticRepository repository;

    @Spy
    private StatisticMapper mapper = new StatisticMapper();

    @InjectMocks
    private StatisticServiceImpl service;

    @Test
    void save() {
        StatisticCreateDataDto createDto = new StatisticCreateDataDto();
        StatisticData statData = new StatisticData();

        when(repository.save(any(StatisticData.class)))
                .thenReturn(statData);

        service.save(createDto);

        verify(repository).save(any(StatisticData.class));
    }

    @Test
    void getHits_whenIpNotUnique_thenFindAllByTimeBetween() {
        when(repository.findAllByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList()))
                .thenReturn(Collections.emptyList());

        List<ViewStatDto> result = service.getHits(LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                List.of("uri"),
                FALSE);

        verify(repository).findAllByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList());

        assertThat(result).isEqualTo(Collections.emptyList());
    }

    @Test
    void getHits_whenIpUnique_thenFindAllUniqueHitByTimeBetween() {
        when(repository.findAllUniqueHitByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList()))
                .thenReturn(Collections.emptyList());

        List<ViewStatDto> result = service.getHits(LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(1),
                List.of("uri"),
                TRUE);

        verify(repository).findAllUniqueHitByTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList());

        assertThat(result).isEqualTo(Collections.emptyList());
    }
}