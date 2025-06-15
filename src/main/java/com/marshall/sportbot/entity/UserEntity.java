package com.marshall.sportbot.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user")
public class UserEntity {
    @Id
    private Long userId;

    private String chatId;

    private String userName;

    private String firstName;

    private String lastName;

    private Boolean shouldSendNotifications;

    private Long dayGoal;

    private Long weekGoal;

    private Long monthGoal;
}