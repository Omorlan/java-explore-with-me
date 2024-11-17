package ru.practicum.mainservice.event.service;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.mainservice.event.model.Event;


public interface StatisticsServiceForEvent {
    void sendStatisticalData(HttpServletRequest request);

    Long getUniqueViews(Event event, String uri);
}
