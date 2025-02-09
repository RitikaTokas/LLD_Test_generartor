package org.example.enums;

public enum DifficultyEnum {
    EASY(1),
    MEDIUM(2),
    HARD(3);

    private final int level;

    DifficultyEnum(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}