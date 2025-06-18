package com.marshall.sportbot.entity;

import jakarta.persistence.Column;
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
@Table(name = "users")
public class UserEntity {
    @Id
    private Long userId;

    @Column(nullable = false)
    private String chatId;

    private String userName;

    private String firstName;

    private String lastName;

    private Boolean shouldSendPushUpNotifications = false;

    private Boolean shouldSendPullUpNotifications = false;

    private Integer pushUpDayGoal;

    private Integer pushUpWeekGoal;

    private Integer pushUpMonthGoal;

    private Integer pullUpDayGoal;

    private Integer pullUpWeekGoal;

    private Integer pullUpMonthGoal;
}