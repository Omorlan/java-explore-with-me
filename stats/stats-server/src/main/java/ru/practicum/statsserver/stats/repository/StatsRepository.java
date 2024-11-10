package ru.practicum.statsserver.stats.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.statsserver.stats.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    List<EndpointHit> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<EndpointHit> findByTimestampBetweenAndUriIn(LocalDateTime start, LocalDateTime end, List<String> uris);
}