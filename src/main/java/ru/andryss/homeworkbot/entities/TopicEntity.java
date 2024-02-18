package ru.andryss.homeworkbot.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entity describing topic
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "topics")
public class TopicEntity {
    /**
     * Topic name
     */
    @Id
    private String name;
    /**
     * User who created this topic
     */
    @ManyToOne
    @JoinColumn(name = "created_user_id", referencedColumnName = "id")
    private UserEntity createdUser;
    /**
     * Creation timestamp
     */
    @Column(name = "create_datetime", nullable = false)
    private LocalDateTime createDatetime;
}
