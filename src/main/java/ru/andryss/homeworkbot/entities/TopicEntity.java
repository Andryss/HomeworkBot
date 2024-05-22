package ru.andryss.homeworkbot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity describing topic
 */
@Entity
@Getter
@Setter
@Table(name = "topics")
public class TopicEntity {
    /**
     * Topic id
     */
    @Id
    String id;
    /**
     * Topic name
     */
    String name;
    /**
     * User who created this topic
     */
    Long createdBy;
    /**
     * Creation timestamp
     */
    Instant createdAt;
}
