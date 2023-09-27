package ru.andryss.homeworkbot.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TopicEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "created_user_id", referencedColumnName = "id")
    private UserEntity createdUser;

    @Column(name = "create_datetime", nullable = false)
    private LocalDateTime createDatetime;
}
