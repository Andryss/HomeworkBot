package ru.andryss.homeworkbot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity describing user submission
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "submissions")
public class SubmissionEntity {
    /**
     * Telegram file identifier
     */
    @Id
    @Column(name = "file_id")
    private String fileId;
    /**
     * File extension (".txt", ".pdf" etc.)
     */
    @Column(name = "extension")
    private String extension;
    /**
     * Upload timestamp
     */
    @Column(name = "upload_datetime", nullable = false)
    private LocalDateTime uploadDatetime;
    /**
     * Topic on which submission was uploaded
     */
    @ManyToOne
    @JoinColumn(name = "topic_name", referencedColumnName = "name")
    private TopicEntity topic;
    /**
     * User which uploaded this submission
     */
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
