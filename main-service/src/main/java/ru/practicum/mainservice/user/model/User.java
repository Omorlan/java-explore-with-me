package ru.practicum.mainservice.user.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

/**
 * User entity model.
 * <p>
 * It stores information about the user, including their name and unique email.
 */
@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    /**
     * User's name.
     * <p>
     * This is the name that the user provided during registration.
     */
    String name;

    /**
     * User's email address.
     * <p>
     * This is the unique email address of the user. This attribute is marked as unique in the database.
     */
    @Column(unique = true)
    String email;
}