package ru.yandex.practicum.taskTracker.model;

public enum Status {
    NEW("Задача открыта"),
    IN_PROGRESS("Задача выполняется"),
    DONE("Задача выполнена");

    private final String name;

    Status(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}