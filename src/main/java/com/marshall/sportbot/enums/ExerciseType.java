package com.marshall.sportbot.enums;

public enum ExerciseType {
    PULL_UP("Подтягивания"),
    PUSH_UP("Отжимания");

    private final String displayName;

    ExerciseType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}