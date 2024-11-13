package ru.practicum.mainservice.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.mainservice.compilation.dto.CompilationDto;
import ru.practicum.mainservice.compilation.dto.NewCompilationDto;
import ru.practicum.mainservice.compilation.dto.UpdateCompilationRequest;
import ru.practicum.mainservice.compilation.mapper.CompilationMapper;
import ru.practicum.mainservice.compilation.model.Compilation;
import ru.practicum.mainservice.compilation.repository.CompilationRepository;
import ru.practicum.mainservice.event.dto.EventShortDto;
import ru.practicum.mainservice.event.mapper.EventMapper;
import ru.practicum.mainservice.event.model.Event;
import ru.practicum.mainservice.event.repository.EventRepository;
import ru.practicum.mainservice.exception.exception.NotFoundException;
import ru.practicum.mainservice.util.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventMapper eventMapper;

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, int from, int size) {
        Pageable pageable = PageRequest.of(from / size, size);
        log.info("Fetching compilations with pinned status: {} (from: {}, size: {})", pinned, from, size);

        Page<Compilation> compilationsPage = (pinned != null)
                ? compilationRepository.findAllByPinned(pinned, pageable)
                : compilationRepository.findAll(pageable);

        if (compilationsPage.isEmpty()) {
            log.warn("No compilations found.");
            return List.of();
        }

        List<CompilationDto> compilationDtos = compilationsPage.getContent().stream()
                .map(compilation -> {
                    List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                            .map(eventMapper::toShortDto).toList();
                    return compilationMapper.toCompilationDto(compilation, eventShortDtos);
                })
                .toList();

        log.info("Successfully fetched {} compilations.", compilationDtos.size());
        return compilationDtos;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        log.info("Fetching compilation with id: {}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.error(Util.compilationNotFound(compId));
                    return new NotFoundException(Util.compilationNotFound(compId));
                });

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(eventMapper::toShortDto).toList();

        log.info("Successfully fetched compilation with id: {}", compId);
        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto newCompilationDto) {
        log.info("Creating new compilation with title: {}", newCompilationDto.getTitle());

        Set<Event> events = new HashSet<>();
        if (newCompilationDto.getEvents() != null) {
            events.addAll(eventRepository.findAllById(newCompilationDto.getEvents()));
        }
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto, events);
        compilation = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(eventMapper::toShortDto)
                .toList();

        log.info("Successfully created new compilation with id: {}", compilation.getId());
        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }

    @Override
    @Transactional
    public void deleteById(Long compId) {
        log.info("Deleting compilation with id: {}", compId);

        compilationRepository.findById(compId).ifPresentOrElse(
                compilation -> {
                    compilationRepository.deleteById(compId);
                    log.info("Successfully deleted compilation with id: {}", compId);
                },
                () -> {
                    log.error(Util.compilationNotFound(compId));
                    throw new NotFoundException(Util.compilationNotFound(compId));
                }
        );
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        log.info("Updating compilation with id: {}", compId);

        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> {
                    log.error(Util.compilationNotFound(compId));
                    return new NotFoundException(String.format(Util.compilationNotFound(compId)));
                });

        StringBuilder updatedFieldsLog = new StringBuilder();

        if (updateCompilationRequest.getTitle() != null) {
            compilation.setTitle(updateCompilationRequest.getTitle());
            updatedFieldsLog.append("Title|");
        }
        if (updateCompilationRequest.getEvents() != null) {
            Set<Event> events = new HashSet<>(eventRepository.findAllById(updateCompilationRequest.getEvents()));
            compilation.setEvents(events);
            updatedFieldsLog.append("Events|");
        }
        if (updateCompilationRequest.getPinned() != null) {
            compilation.setPinned(updateCompilationRequest.getPinned());
            updatedFieldsLog.append("Pinned|");
        }

        String updatedFields = updatedFieldsLog.toString().replaceAll("\\|$", "").replace("|", ", ");
        log.info("Updated fields for compilation with id {}: {}", compId, updatedFields);

        compilation = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                .map(eventMapper::toShortDto)
                .toList();

        log.info("Successfully updated compilation with id: {}", compId);
        return compilationMapper.toCompilationDto(compilation, eventShortDtos);
    }
}
