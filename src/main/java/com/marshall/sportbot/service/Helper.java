package com.marshall.sportbot.service;

import lombok.experimental.UtilityClass;

import java.time.DayOfWeek;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@UtilityClass
public class Helper {
    public ZonedDateTime getStartOfDay() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime start = now.withHour(3).withMinute(0).withSecond(0).withNano(0);
        return now.isBefore(start) ? start.minusDays(1) : start;
    }

    public ZonedDateTime getStartOfWeek() {
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        ZonedDateTime start = now.withHour(3).withMinute(0).withSecond(0).withNano(0);
        DayOfWeek currentDay = now.getDayOfWeek();

        int daysToSubtract = currentDay.getValue() - DayOfWeek.MONDAY.getValue();
        if (daysToSubtract < 0) daysToSubtract += 7;

        ZonedDateTime weekStart = start.minusDays(daysToSubtract);
        return now.isBefore(start) ? weekStart.minusDays(1) : weekStart;
    }

    public int parseCount(String text) {
        int count = Integer.parseInt(text);
        if (count < 0) {
            return 0;
        }
        return count;
    }
}
