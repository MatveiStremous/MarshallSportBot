package com.marshall.sportbot.service;

import com.marshall.sportbot.controller.SportBotController;
import com.marshall.sportbot.entity.UserEntity;
import com.marshall.sportbot.enums.ExerciseType;
import com.marshall.sportbot.repository.ExerciseRepository;
import com.marshall.sportbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    private final SportBotController bot;

    @Scheduled(cron = "0 0 19,21,23 * * *", zone = "Europe/Minsk")
    public void scheduledPushUpReminders() {
        sendPushUpReminderIfNeeded();
    }

    public void sendPushUpReminderIfNeeded() {
        ZonedDateTime startOfDay = Helper.getStartOfDay();

        Map<Long, Integer> userPushUpMap = exerciseRepository
                .getUserPushUpSumsFromTime(ExerciseType.PUSH_UP, startOfDay)
                .stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).intValue()
                ));

        List<UserEntity> users = userRepository.findAllByShouldSendPushUpNotificationsTrue();

        for (UserEntity user : users) {
            Integer dayGoal = user.getPushUpDayGoal();
            if (dayGoal == null || dayGoal == 0) continue;

            int doneToday = userPushUpMap.getOrDefault(user.getUserId(), 0);

            if (doneToday < dayGoal) {
                int remaining = dayGoal - doneToday;
                String message = String.format(
                        "Напоминание: сегодня вы сделали %d из %d отжиманий. Осталось %d!",
                        doneToday, dayGoal, remaining
                );
                bot.sendMessage(user.getChatId(), message);
            }
        }
    }
}
