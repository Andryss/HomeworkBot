package ru.andryss.homeworkbot.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

/**
 * Entity describing user submission
 */
@Entity
@Getter
@Setter
@Table(name = "submissions")
public class SubmissionEntity {
    /**
     * Telegram file identifier
     */
    @Id
    String fileId;
    /**
     * File extension (".txt", ".pdf" etc.)
     */
    String extension;
    /**
     * Upload timestamp
     */
    Instant uploadedAt;
    /**
     * Topic on which submission was uploaded
     */
    String topicId;
    /**
     * User which uploaded this submission
     */
    Long userId;
}
