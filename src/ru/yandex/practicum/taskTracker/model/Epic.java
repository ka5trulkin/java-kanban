package ru.yandex.practicum.taskTracker.model;

import java.util.HashMap;

public class Epic extends Task{
    private final HashMap<Integer, Status> epicSubtaskInfo = new HashMap<>();

    public Epic(String taskName, String description, int id) {
        super(taskName, description, id);
    }

    public HashMap<Integer, Status> getEpicSubtaskInfo() {
        return epicSubtaskInfo;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "epicSubtasks=" + epicSubtaskInfo +
                "} " + super.toString();
    }
}