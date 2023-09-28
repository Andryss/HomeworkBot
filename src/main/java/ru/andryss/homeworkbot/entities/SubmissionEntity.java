package ru.andryss.homeworkbot.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "submissions")
public class SubmissionEntity {
    @Id
    @Column(name = "file_id")
    private String fileId;

    @Column(name = "upload_datetime", nullable = false)
    private LocalDateTime uploadDatetime;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private TopicEntity topic;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private UserEntity user;
}
