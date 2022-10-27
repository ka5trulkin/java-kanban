package ru.yandex.practicum.taskTracker.model;

import java.util.HashMap;

public class Epic extends Task{
    private final HashMap<Integer, Subtask> epicSubtasks = new HashMap<>();

    public Epic(String taskName, String description, int id) {
        super(taskName, description, id);
    }

    public HashMap<Integer, Subtask> getEpicSubtasks() {
        return epicSubtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "EPIC_SUBTASKS=" + epicSubtasks +
                "} " + super.toString();
    }
}