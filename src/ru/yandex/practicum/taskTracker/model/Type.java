package ru.yandex.practicum.taskTracker.model;

public enum Type {
    TASK("task"),
    EPIC("epic"),
    SUBTASK("subtask"),
    HISTORY("history");

    final String lowerCase;

    Type(String lowerCase) {
        this.lowerCase = lowerCase;
    }

    public String toLowerCase() {
        return lowerCase;
    }
}