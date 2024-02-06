package ru.andryss.homeworkbot.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity describing user
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "users")
public class UserEntity {
    /**
     * Telegram user identifier
     */
    @Id
    private Long id;
    /**
     * Name, surname, patronymic
     */
    private String name;
}
