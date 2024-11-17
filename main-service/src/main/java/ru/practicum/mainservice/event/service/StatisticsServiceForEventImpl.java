package ru.practicum.mainservice.event.service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.util.Util;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceForEventImpl implements StatisticsServiceForEvent {
    private final StatsClient statsClient;
    private final ObjectMapper objectMapper;
    @Value("${spring.application.name}")
    private String serviceId;

    @Override
    public void sendStatisticalData(HttpServletRequest request) {
        EndpointHitDto stat = EndpointHitDto.builder()
                .app(serviceId)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build();

        statsClient.create(stat);
    }

    @Override
    public Long getUniqueViews(Event event, String uri) {
        String startDate = event.getCreatedOn().format(Util.DATE_TIME_FORMATTER);
        String endDate = LocalDateTime.now().format(Util.DATE_TIME_FORMATTER);
        List<String> uris = List.of(uri);

        List<ViewStatsDto> stats = convertResponseToList(statsClient.getStats(startDate, endDate, uris, true));

        return stats.isEmpty()
                ? 0L
                : stats.stream().mapToLong(ViewStatsDto::getHits).sum();
    }

    private List<ViewStatsDto> convertResponseToList(ResponseEntity<Object> response) {
        if (response.getBody() == null) {
            return List.of();
        }
        try {
            return objectMapper.convertValue(response.getBody(), new TypeReference<>() {
            });
        } catch (Exception e) {
            throw new IllegalStateException("Failed to convert response to list", e);
        }
    }
}
