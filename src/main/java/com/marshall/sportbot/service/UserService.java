package com.marshall.sportbot.service;

import com.marshall.sportbot.entity.UserEntity;
import com.marshall.sportbot.enums.ExerciseType;
import com.marshall.sportbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserEntity getUser(User user, String chatId) {
        Long userId = user.getId();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            userEntity = UserEntity.builder()
                    .userId(userId)
                    .chatId(chatId)
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userName(user.getUserName())
                    .shouldSendPullUpNotifications(false)
                    .shouldSendPushUpNotifications(false)
                    .build();
        } else {
            userEntity.setChatId(chatId);
        }
        return userRepository.save(userEntity);
    }

    public String getGoalsMessage(UserEntity userEntity, ExerciseType exerciseType) {
        StringBuilder sb = new StringBuilder();
        Integer dayGoal = null, weekGoal = null, monthGoal = null;
        if (ExerciseType.PUSH_UP.equals(exerciseType)) {
            dayGoal = userEntity.getPushUpDayGoal();
            weekGoal = userEntity.getPushUpWeekGoal();
            monthGoal = userEntity.getPushUpMonthGoal();
            if (dayGoal == null && weekGoal == null && monthGoal == null) {
                return "У вас не установлены цели для отжиманий";
            }
        } else if (ExerciseType.PULL_UP.equals(exerciseType)) {
            dayGoal = userEntity.getPullUpDayGoal();
            weekGoal = userEntity.getPullUpWeekGoal();
            monthGoal = userEntity.getPullUpMonthGoal();
            if (dayGoal == null && weekGoal == null && monthGoal == null) {
                return "У вас не установлены цели для подтягиваний";
            }
        }
        sb.append("Ваши цели");
        if (dayGoal != null) {
            sb.append("\nДневная - ").append(dayGoal);
        }
        if (weekGoal != null) {
            sb.append("\nНедельная - ").append(weekGoal);
        }
        if (monthGoal != null) {
            sb.append("\nМесячная - ").append(monthGoal);
        }

        return sb.toString();
    }

    public UserEntity setDayGoal(UserEntity userEntity, ExerciseType exerciseType, int count) {
        if (ExerciseType.PUSH_UP.equals(exerciseType)) {
            userEntity.setPushUpDayGoal(count);
        } else if (ExerciseType.PULL_UP.equals(exerciseType)) {
            userEntity.setPullUpDayGoal(count);
        }
        return userRepository.save(userEntity);
    }

    public UserEntity setWeekGoal(UserEntity userEntity, ExerciseType exerciseType, int count) {
        if (ExerciseType.PUSH_UP.equals(exerciseType)) {
            userEntity.setPushUpWeekGoal(count);
        } else if (ExerciseType.PULL_UP.equals(exerciseType)) {
            userEntity.setPullUpWeekGoal(count);
        }
        return userRepository.save(userEntity);
    }

    public UserEntity setMonthGoal(UserEntity userEntity, ExerciseType exerciseType, int count) {
        if (ExerciseType.PUSH_UP.equals(exerciseType)) {
            userEntity.setPushUpMonthGoal(count);
        } else if (ExerciseType.PULL_UP.equals(exerciseType)) {
            userEntity.setPullUpMonthGoal(count);
        }
        return userRepository.save(userEntity);
    }

    public String switchNotification(UserEntity userEntity, ExerciseType exerciseType) {
        String message = null;
        if (ExerciseType.PUSH_UP.equals(exerciseType)) {
            userEntity.setShouldSendPushUpNotifications(!userEntity.getShouldSendPushUpNotifications());
            message = "Напоминания по отжиманиям " + (userEntity.getShouldSendPushUpNotifications() ? "включены" : "выключены");
        } else if (ExerciseType.PULL_UP.equals(exerciseType)) {
            userEntity.setShouldSendPullUpNotifications(!userEntity.getShouldSendPullUpNotifications());
            message = "Напоминания по подтягиваниям " + (userEntity.getShouldSendPullUpNotifications() ? "включены" : "выключены");
        }
        userRepository.save(userEntity);
        return message;
    }
}
