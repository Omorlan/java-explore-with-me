package ru.practicum.mainservice.event.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.mainservice.category.model.Category;
import ru.practicum.mainservice.user.model.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    /**
     * Unique identifier of the event.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * Short annotation or summary of the event.
     */
    @Column(nullable = false, length = 2000)
    String annotation;

    /**
     * Category of the event. Linked with the category entity.
     */
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    /**
     * Number of confirmed requests to join the event.
     */
    @Column(name = "confirmed_requests")
    Long confirmedRequests;

    /**
     * Date and time when the event was created.
     */
    @Column(name = "created_on", nullable = false)
    LocalDateTime createdOn;

    /**
     * Detailed description of the event.
     */
    @Column(nullable = false, length = 7000)
    String description;

    /**
     * Date and time when the event will take place.
     */
    @Column(name = "event_date", nullable = false)
    LocalDateTime eventDate;

    /**
     * The user who initiated or created the event.
     */
    @ManyToOne
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    /**
     * Location details where the event will occur.
     */
    @Embedded
    Location location;

    /**
     * Indicates whether participation in the event is paid.
     */
    @Column(nullable = false)
    boolean paid;

    /**
     * Limit on the number of participants allowed to join the event. Defaults to 0 (no limit).
     */
    @Builder.Default
    @Column(name = "participant_limit", nullable = false)
    Long participantLimit = 0L;

    /**
     * Date and time when the event was published.
     */
    @Column(name = "published_on")
    LocalDateTime publishedOn;

    /**
     * Whether moderation is required for participant requests. Defaults to true.
     */
    @Builder.Default
    @Column(name = "request_moderation", nullable = false)
    boolean requestModeration = true;

    /**
     * Current state of the event (e.g., PENDING, PUBLISHED).
     */
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    State state;

    /**
     * Title of the event.
     */
    @Column(nullable = false, length = 120)
    String title;

    /**
     * Number of views the event has received.
     */
    Long views;
}