package ru.practicum.mainservice.compilation.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.event.model.Event;

import java.util.Set;

/**
 * Represents a compilation of events. A compilation is a collection of events
 * that are grouped together under a common title and can be pinned for visibility.
 *
 * This entity is stored in the "compilations" table in the database.
 */
@Entity
@Table(name = "compilations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Compilation {

    /**
     * Unique identifier for the compilation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * The title of the compilation. It cannot be null.
     */
    @Column(nullable = false)
    String title;

    /**
     * A set of events associated with the compilation.
     * A compilation can have multiple events.
     * The relationship is many-to-many, meaning events can be shared between multiple compilations.
     */
    @ManyToMany
    @JoinTable(
            name = "compilation_events",
            joinColumns = @JoinColumn(name = "compilation_id"),
            inverseJoinColumns = @JoinColumn(name = "event_id")
    )
    Set<Event> events;

    /**
     * Flag indicating whether the compilation is pinned.
     * Pinned compilations are typically highlighted for easier access.
     * This field cannot be null.
     */
    @Column(nullable = false)
    boolean pinned;

}
