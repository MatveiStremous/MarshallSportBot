package com.marshall.sportbot.service;

import com.marshall.sportbot.entity.ExerciseEntity;
import com.marshall.sportbot.entity.UserEntity;
import com.marshall.sportbot.enums.ExerciseType;
import com.marshall.sportbot.repository.ExerciseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public String getExerciseStatsMessage(UserEntity user, ExerciseType type) {
        List<ExerciseEntity> allUserExercises = exerciseRepository.findAllFromTime(
                user.getUserId(), type, ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZonedDateTime.now().getZone())
        );

        if (allUserExercises.isEmpty()) {
            return "У вас пока нет данных по упражнению: " + type.getDisplayName();
        }

        int totalCount = allUserExercises.stream()
                .mapToInt(e -> e.getCount() != null ? e.getCount() : 0)
                .sum();

        int maxReps = allUserExercises.stream()
                .mapToInt(e -> e.getCount() != null ? e.getCount() : 0)
                .max()
                .orElse(0);

        Map<ZonedDateTime, Integer> dailyTotals = allUserExercises.stream()
                .collect(Collectors.groupingBy(
                        e -> e.getDateTime().toLocalDate().atStartOfDay(e.getDateTime().getZone()),
                        Collectors.summingInt(e -> e.getCount() != null ? e.getCount() : 0)
                ));

        var bestDayEntry = dailyTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        Map<ZonedDateTime, Integer> weeklyTotals = allUserExercises.stream()
                .collect(Collectors.groupingBy(
                        e -> Helper.getStartOfWeek(e.getDateTime()),
                        Collectors.summingInt(e -> e.getCount() != null ? e.getCount() : 0)
                ));

        var bestWeekEntry = weeklyTotals.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .orElse(null);

        List<ExerciseEntity> recentExercises = getWeekExercises(user, type);
        int todayCount = countSince(recentExercises, Helper.getStartOfDay());
        int weekCount = countSince(recentExercises, Helper.getStartOfWeek());

        StringBuilder result = new StringBuilder();
        result.append("📊 *Статистика ваших ").append(type.getDisplayName()).append("*\n\n");
        result.append("• Всего повторений: ").append(totalCount).append("\n");
        result.append("• Максимум за подход: ").append(maxReps).append("\n");

        if (bestDayEntry != null) {
            result.append("• Лучший день: ")
                    .append(bestDayEntry.getValue())
                    .append(" (")
                    .append(bestDayEntry.getKey().toLocalDate())
                    .append(")\n");
        }

        if (bestWeekEntry != null) {
            result.append("• Лучшая неделя: ")
                    .append(bestWeekEntry.getValue())
                    .append(" (")
                    .append(bestWeekEntry.getKey().toLocalDate())
                    .append(" - ")
                    .append(bestWeekEntry.getKey().plusDays(6).toLocalDate())
                    .append(")\n");
        }

        result.append("\n📆 *Текущая статистика*\n");
        result.append("• За сегодня: ").append(todayCount).append("\n");
        result.append("• За неделю: ").append(weekCount).append("\n");

        return result.toString();
    }


    public String addExercise(UserEntity userEntity, ExerciseType exerciseType, int count) {
        ExerciseEntity exerciseEntity = ExerciseEntity.builder()
                .userId(userEntity.getUserId())
                .dateTime(ZonedDateTime.now())
                .count(count)
                .exerciseType(exerciseType)
                .build();
        exerciseEntity = exerciseRepository.save(exerciseEntity);
        return "Ваш подход сохранён под номером " + exerciseEntity.getId() + ". Зафиксировано отжиманий: " + count;
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
