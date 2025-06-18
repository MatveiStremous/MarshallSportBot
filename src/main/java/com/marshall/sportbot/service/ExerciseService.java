package com.marshall.sportbot.service;

import com.marshall.sportbot.entity.ExerciseEntity;
import com.marshall.sportbot.entity.UserEntity;
import com.marshall.sportbot.enums.ExerciseType;
import com.marshall.sportbot.enums.UserState;
import com.marshall.sportbot.exception.BusinessException;
import com.marshall.sportbot.repository.ExerciseRepository;
import com.marshall.sportbot.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ExerciseService {
    private final ExerciseRepository exerciseRepository;

    public String getExerciseProgressMessage(UserEntity user, ExerciseType type) {
        int dayGoal = getDayGoal(user, type);
        int weekGoal = getWeekGoal(user, type);

        List<ExerciseEntity> weekExercises = getWeekExercises(user, type);

        int todayCount = countSince(weekExercises, Helper.getStartOfDay());
        int weekCount = countSince(weekExercises, Helper.getStartOfWeek());

        List<String> result = new ArrayList<>();

        if (dayGoal > 0) {
            result.add("За сегодня: " + todayCount + " из " + dayGoal);
        } else {
            result.add("За сегодня: " + todayCount);
        }

        if (weekGoal > 0) {
            result.add("За неделю: " + weekCount + " из " + weekGoal);
        } else {
            result.add("За неделю: " + weekCount);
        }

        return String.join("\n", result);
    }

    public String addExercise(UserEntity userEntity, ExerciseType exerciseType, int count) {
        ExerciseEntity exerciseEntity = ExerciseEntity.builder()
                .userId(userEntity.getUserId())
                .dateTime(ZonedDateTime.now())
                .count(count)
                .exerciseType(exerciseType)
                .build();
        exerciseEntity = exerciseRepository.save(exerciseEntity);
        return "Ваш подход сохранён под номером " + exerciseEntity.getId();
    }

    private int getDayGoal(UserEntity user, ExerciseType type) {
        return switch (type) {
            case PUSH_UP -> user.getPushUpDayGoal() != null ? user.getPushUpDayGoal() : 0;
            case PULL_UP -> user.getPullUpDayGoal() != null ? user.getPullUpDayGoal() : 0;
        };
    }

    private int getWeekGoal(UserEntity user, ExerciseType type) {
        return switch (type) {
            case PUSH_UP -> user.getPushUpWeekGoal() != null ? user.getPushUpWeekGoal() : 0;
            case PULL_UP -> user.getPullUpWeekGoal() != null ? user.getPullUpWeekGoal() : 0;
        };
    }

    private List<ExerciseEntity> getWeekExercises(UserEntity user, ExerciseType type) {
        return exerciseRepository.findAllFromTime(
                user.getUserId(), type, Helper.getStartOfWeek()
        );
    }

    private int countSince(List<ExerciseEntity> list, ZonedDateTime from) {
        return list.stream()
                .filter(e -> e.getDateTime().isAfter(from) || e.getDateTime().isEqual(from))
                .mapToInt(e -> e.getCount() != null ? e.getCount() : 0)
                .sum();
    }
}
