package ru.practicum.statsserver.stats.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.statsserver.stats.mapper.EndpointHitMapper;
import ru.practicum.statsserver.stats.model.EndpointHit;
import ru.practicum.statsserver.stats.repository.StatsRepository;
import ru.practicum.statsserver.stats.util.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {
    private final StatsRepository statsRepository;
    private final EndpointHitMapper endpointHitMapper;

    /**
     * Creates a new visit record in the database.
     *
     * @param endpointHitDto DTO object containing visit data.
     */
    @Override
    @Transactional
    public void create(EndpointHitDto endpointHitDto) {
        log.info("Saving endpoint hit: {}", endpointHitDto);
        EndpointHit endpointHit = endpointHitMapper.toEndpointHit(endpointHitDto);
        statsRepository.save(endpointHit);
        log.info("Endpoint hit saved successfully");
    }

    /**
     * Retrieves a list of statistics based on the specified parameters.
     * The statistics include the number of hits per URI within a given time range.
     * If unique is true, only unique hits per IP are counted.
     *
     * @param start  Start date and time as a string (format: "yyyy-MM-dd HH:mm:ss").
     * @param end    End date and time as a string (format: "yyyy-MM-dd HH:mm:ss").
     * @param uris   List of URIs to filter by. If null or empty, all URIs are included.
     * @param unique Flag indicating whether to count only unique hits per IP.
     * @return A list of {@link ViewStatsDto} objects representing statistics for each URI.
     */
    @Override
    public List<ViewStatsDto> getStats(String start,
                                       String end,
                                       List<String> uris,
                                       boolean unique) {
        LocalDateTime startTime = parseDate(start);
        LocalDateTime endTime = parseDate(end);
        log.info("Fetching stats from {} to {} for URIs {} with unique flag set to {}",
                startTime, endTime, uris, unique);
        List<EndpointHit> hits = new ArrayList<>();

        if (uris != null && !uris.isEmpty()) {
            hits.addAll(statsRepository.findByTimestampBetweenAndUriIn(startTime, endTime, uris));
        } else {
            hits.addAll(statsRepository.findByTimestampBetween(startTime, endTime));
        }

        if (unique) {
            Set<String> uniqueIps = new HashSet<>();
            hits.removeIf(hit -> !uniqueIps.add(hit.getIp()));
        }

        List<ViewStatsDto> statsDtos = endpointHitMapper.toViewStatsDtoList(hits);
        log.info("Fetched {} stats entries", statsDtos.size());

        return statsDtos;
    }

    /**
     * Parses a string date into a {@link LocalDateTime} object.
     *
     * @param date a string representing the date in the format "yyyy-MM-dd HH:mm:ss".
     * @return a {@link LocalDateTime} object.
     * @throws IllegalArgumentException if the date format is invalid.
     */
    private LocalDateTime parseDate(String date) {
        try {
            return LocalDateTime.parse(date, DateTimeUtil.DATE_TIME_FORMATTER);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid date format. Expected: yyyy-MM-dd HH:mm:ss");
        }
    }
}