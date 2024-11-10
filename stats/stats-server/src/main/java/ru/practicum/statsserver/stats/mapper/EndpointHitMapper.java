package ru.practicum.statsserver.stats.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.statsserver.stats.model.EndpointHit;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class EndpointHitMapper {

    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        return EndpointHit.builder()
                .app(dto.getApp())
                .uri(dto.getUri())
                .ip(dto.getIp())
                .timestamp(dto.getTimestamp())
                .build();
    }

    public List<ViewStatsDto> toViewStatsDtoList(Collection<EndpointHit> hits) {
        Map<String, Map<String, Long>> groupedStats = hits.stream()
                .collect(Collectors.groupingBy(EndpointHit::getApp,
                        Collectors.groupingBy(EndpointHit::getUri, Collectors.counting())));

        return groupedStats.entrySet().stream()
                .flatMap(appEntry -> appEntry.getValue().entrySet().stream()
                        .map(uriEntry -> ViewStatsDto.builder()
                                .app(appEntry.getKey())
                                .uri(uriEntry.getKey())
                                .hits(uriEntry.getValue().intValue())
                                .build()))
                .sorted((dto1, dto2) -> Integer.compare(dto2.getHits(), dto1.getHits()))
                .toList();
    }
}
